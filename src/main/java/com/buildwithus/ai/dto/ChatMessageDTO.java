package com.buildwithus.ai.dto;

import com.buildwithus.ai.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private ChatMessage.MessageRole role;
    private String content;
    private LocalDateTime createdAt;
}