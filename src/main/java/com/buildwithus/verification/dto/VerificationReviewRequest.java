package com.buildwithus.verification.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationReviewRequest {
    
    @NotNull(message = "Approval decision is required")
    private Boolean approve;
    
    @Size(max = 1000, message = "Admin notes must be less than 1000 characters")
    private String adminNotes;
}