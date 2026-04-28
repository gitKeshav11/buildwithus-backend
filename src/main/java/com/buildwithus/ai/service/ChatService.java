package com.buildwithus.ai.service;

import com.buildwithus.ai.dto.ChatConversationDTO;
import com.buildwithus.ai.dto.ChatMessageDTO;
import com.buildwithus.ai.dto.CreateConversationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    ChatConversationDTO createConversation(Long userId, CreateConversationRequest request);
    ChatMessageDTO sendMessage(Long conversationId, Long userId, String message);
    ChatConversationDTO getConversationById(Long conversationId, Long userId);
    Page<ChatConversationDTO> getMyConversations(Long userId, Pageable pageable);
    void deleteConversation(Long conversationId, Long userId);
    long getTotalConversationsCount();
}