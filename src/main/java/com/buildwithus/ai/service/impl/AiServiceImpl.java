package com.buildwithus.ai.service.impl;

import com.buildwithus.ai.dto.CodeReviewRequestDTO;
import com.buildwithus.ai.entity.ChatMessage;
import com.buildwithus.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Override
    public CodeReviewRequestDTO performCodeReview(String code, String language, String context) {

        String prompt = "Review this code and give:\n" +
                "1. Summary\n2. Issues\n3. Improvements\n4. Security\n5. Optimization\n6. Score\n\n" +
                "Code:\n" + code;

        String response = callGroq(List.of(
                Map.of("role", "user", "content", prompt)
        ));

        return CodeReviewRequestDTO.builder()
                .reviewSummary(response)
                .qualityScore(80)
                .isProcessed(true)
                .build();
    }

    @Override
    public String generateChatResponse(List<ChatMessage> history, String userMessage) {

        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", "You are an expert AI developer assistant. Help with coding, projects, debugging, career."
        ));

        for (ChatMessage msg : history) {
            String role = msg.getRole() == ChatMessage.MessageRole.USER ? "user" : "assistant";
            messages.add(Map.of("role", role, "content", msg.getContent()));
        }

        messages.add(Map.of("role", "user", "content", userMessage));

        return callGroq(messages);
    }

    @Override
    public String generateConversationTitle(String firstMessage) {
        return callGroq(List.of(
                Map.of("role", "user", "content", "Give short title: " + firstMessage)
        ));
    }

    private String callGroq(List<Map<String, String>> messages) {

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "llama-3.3-70b-versatile"); //  best Groq model
            body.put("messages", messages);

            Map<String, Object> response = webClient.post()
                    .uri(GROQ_API_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return message.get("content").toString();
                }
            }

        } catch (Exception e) {
            log.error("Groq API error", e);
        }

        return "AI is currently unavailable. Try again.";
    }
}