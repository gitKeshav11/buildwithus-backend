package com.buildwithus.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationDTO {
    private Long id;
    private String title;
    private Boolean isActive;
    private List<ChatMessageDTO> messages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}