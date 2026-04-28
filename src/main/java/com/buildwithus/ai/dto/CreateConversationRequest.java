package com.buildwithus.ai.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @Size(max = 10000, message = "Initial message must be less than 10000 characters")
    private String initialMessage;
}