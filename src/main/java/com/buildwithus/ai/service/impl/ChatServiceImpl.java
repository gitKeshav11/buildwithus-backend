package com.buildwithus.ai.service.impl;

import com.buildwithus.ai.dto.ChatConversationDTO;
import com.buildwithus.ai.dto.ChatMessageDTO;
import com.buildwithus.ai.dto.CreateConversationRequest;
import com.buildwithus.ai.entity.ChatConversation;
import com.buildwithus.ai.entity.ChatMessage;
import com.buildwithus.ai.repository.ChatConversationRepository;
import com.buildwithus.ai.repository.ChatMessageRepository;
import com.buildwithus.ai.service.AiService;
import com.buildwithus.ai.service.ChatService;
import com.buildwithus.exception.ForbiddenException;
import com.buildwithus.exception.RateLimitExceededException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.rate-limit.ai-requests-per-hour:20}")
    private int aiRequestsPerHour;

    @Override
    public ChatConversationDTO createConversation(Long userId, CreateConversationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String title = request.getTitle();

        if (title == null || title.isBlank()) {
            title = "New Chat";
        }

        ChatConversation conversation = ChatConversation.builder()
                .user(user)
                .title(title)
                .isActive(true)
                .build();

        conversation = conversationRepository.save(conversation);

        if (request.getInitialMessage() != null && !request.getInitialMessage().isBlank()) {
            sendMessage(conversation.getId(), userId, request.getInitialMessage());

            if (request.getTitle() == null || request.getTitle().isBlank()) {
                try {
                    String generatedTitle = aiService.generateConversationTitle(request.getInitialMessage());
                    conversation.setTitle(generatedTitle);
                    conversation = conversationRepository.save(conversation);
                } catch (Exception e) {
                    log.warn("Failed to generate conversation title", e);
                }
            }
        }

        return toDTO(conversation);
    }

    @Override
    public ChatMessageDTO sendMessage(Long conversationId, Long userId, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        checkRateLimit(userId);

        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatConversation", "id", conversationId));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to send messages in this conversation");
        }

        ChatMessage userMessage = ChatMessage.builder()
                .conversation(conversation)
                .role(ChatMessage.MessageRole.USER)
                .content(message.trim())
                .build();

        messageRepository.save(userMessage);

        List<ChatMessage> history =
                messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        String aiResponse = aiService.generateChatResponse(history, message.trim());

        ChatMessage assistantMessage = ChatMessage.builder()
                .conversation(conversation)
                .role(ChatMessage.MessageRole.ASSISTANT)
                .content(aiResponse)
                .build();

        assistantMessage = messageRepository.save(assistantMessage);

        incrementRateLimit(userId);

        return toMessageDTO(assistantMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatConversationDTO getConversationById(Long conversationId, Long userId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatConversation", "id", conversationId));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to view this conversation");
        }

        return toDTO(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatConversationDTO> getMyConversations(Long userId, Pageable pageable) {
        return conversationRepository.findByUserId(userId, pageable).map(this::toDTO);
    }

    @Override
    public void deleteConversation(Long conversationId, Long userId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatConversation", "id", conversationId));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this conversation");
        }

        conversation.setIsDeleted(true);
        conversation.setIsActive(false);
        conversationRepository.save(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalConversationsCount() {
        return conversationRepository.countAllConversations();
    }

    private void checkRateLimit(Long userId) {
        try {
            String key = "rate_limit:chat:" + userId;
            Object value = redisTemplate.opsForValue().get(key);

            Integer count = value instanceof Integer
                    ? (Integer) value
                    : value != null ? Integer.parseInt(value.toString()) : null;

            if (count != null && count >= aiRequestsPerHour) {
                throw new RateLimitExceededException(
                        "You have exceeded the maximum number of chat messages per hour. Please try again later."
                );
            }
        } catch (RateLimitExceededException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Redis rate-limit check failed. Continuing without blocking.", e);
        }
    }

    private void incrementRateLimit(Long userId) {
        try {
            String key = "rate_limit:chat:" + userId;
            redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, Duration.ofHours(1));
        } catch (Exception e) {
            log.warn("Redis rate-limit increment failed", e);
        }
    }

    private ChatConversationDTO toDTO(ChatConversation conversation) {
        List<ChatMessageDTO> messages = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversation.getId())
                .stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());

        return ChatConversationDTO.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .isActive(conversation.getIsActive())
                .messages(messages)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private ChatMessageDTO toMessageDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}