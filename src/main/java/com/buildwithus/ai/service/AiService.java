package com.buildwithus.ai.service;

import com.buildwithus.ai.dto.CodeReviewRequestDTO;
import com.buildwithus.ai.entity.ChatMessage;

import java.util.List;

public interface AiService {
    CodeReviewRequestDTO performCodeReview(String code, String language, String context);
    String generateChatResponse(List<ChatMessage> conversationHistory, String userMessage);
    String generateConversationTitle(String firstMessage);
}