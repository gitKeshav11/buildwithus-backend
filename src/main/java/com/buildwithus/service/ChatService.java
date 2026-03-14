package com.buildwithus.service;

import com.buildwithus.entity.ChatMessage;
import com.buildwithus.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AIService aiService;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       AIService aiService) {
        this.chatMessageRepository = chatMessageRepository;
        this.aiService = aiService;
    }

    public String processMessage(Long sessionId,String message){

        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(message);

        chatMessageRepository.save(userMsg);

        String aiReply = aiService.askAI(message);

        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setSessionId(sessionId);
        aiMsg.setRole("assistant");
        aiMsg.setContent(aiReply);

        chatMessageRepository.save(aiMsg);

        return aiReply;
    }

    public List<ChatMessage> getHistory(Long sessionId){
        return chatMessageRepository.findBySessionId(sessionId);
    }

}