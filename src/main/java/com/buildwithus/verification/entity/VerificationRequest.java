package com.buildwithus.verification.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.VerificationStatus;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "verification_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequest extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING;
    
    @Column(name = "github_linked")
    private Boolean githubLinked = false;
    
    @Column(name = "linkedin_linked")
    private Boolean linkedinLinked = false;
    
    @Column(name = "portfolio_submitted")
    private Boolean portfolioSubmitted = false;
    
    @Column(name = "profile_complete")
    private Boolean profileComplete = false;
    
    @Column(name = "has_projects")
    private Boolean hasProjects = false;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
}