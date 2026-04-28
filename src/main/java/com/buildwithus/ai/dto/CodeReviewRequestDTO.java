package com.buildwithus.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewRequestDTO {
    private Long id;
    private String title;
    private String codeContent;
    private String programmingLanguage;
    private String contextDescription;
    private String reviewSummary;
    private String detectedIssues;
    private String improvementSuggestions;
    private String securityObservations;
    private String optimizationSuggestions;
    private String improvedCode;
    private Integer qualityScore;
    private Boolean isProcessed;
    private LocalDateTime createdAt;
}