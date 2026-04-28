package com.buildwithus.verification.dto;

import com.buildwithus.common.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequestDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private VerificationStatus status;
    private Boolean githubLinked;
    private Boolean linkedinLinked;
    private Boolean portfolioSubmitted;
    private Boolean profileComplete;
    private Boolean hasProjects;
    private String adminNotes;
    private LocalDateTime createdAt;
}