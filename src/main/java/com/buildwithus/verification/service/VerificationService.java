package com.buildwithus.verification.service;

import com.buildwithus.verification.dto.VerificationRequestDTO;
import com.buildwithus.verification.dto.VerificationReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VerificationService {
    VerificationRequestDTO requestVerification(Long userId);
    VerificationRequestDTO getMyVerificationStatus(Long userId);
    Page<VerificationRequestDTO> getPendingVerificationRequests(Pageable pageable);
    VerificationRequestDTO reviewVerificationRequest(Long requestId, Long adminId, VerificationReviewRequest request);
}