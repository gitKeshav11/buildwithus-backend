package com.buildwithus.ai.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "code_review_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeReviewRequest extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String title;
    
    @Column(name = "code_content", columnDefinition = "TEXT", nullable = false)
    private String codeContent;
    
    @Column(name = "programming_language")
    private String programmingLanguage;
    
    @Column(name = "context_description", columnDefinition = "TEXT")
    private String contextDescription;
    
    @Column(name = "review_summary", columnDefinition = "TEXT")
    private String reviewSummary;
    
    @Column(name = "detected_issues", columnDefinition = "TEXT")
    private String detectedIssues;
    
    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;
    
    @Column(name = "security_observations", columnDefinition = "TEXT")
    private String securityObservations;
    
    @Column(name = "optimization_suggestions", columnDefinition = "TEXT")
    private String optimizationSuggestions;
    
    @Column(name = "improved_code", columnDefinition = "TEXT")
    private String improvedCode;
    
    @Column(name = "quality_score")
    private Integer qualityScore;
    
    @Column(name = "is_processed")
    private Boolean isProcessed = false;
}