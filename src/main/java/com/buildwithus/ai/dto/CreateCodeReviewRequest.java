package com.buildwithus.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCodeReviewRequest {
    
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @NotBlank(message = "Code content is required")
    @Size(max = 50000, message = "Code content must be less than 50000 characters")
    private String codeContent;
    
    @Size(max = 50, message = "Programming language must be less than 50 characters")
    private String programmingLanguage;
    
    @Size(max = 2000, message = "Context description must be less than 2000 characters")
    private String contextDescription;
}