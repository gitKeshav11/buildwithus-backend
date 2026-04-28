package com.buildwithus.ai.service;

import com.buildwithus.ai.dto.CodeReviewRequestDTO;
import com.buildwithus.ai.dto.CreateCodeReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CodeReviewService {
    CodeReviewRequestDTO submitCodeReview(Long userId, CreateCodeReviewRequest request);
    CodeReviewRequestDTO getCodeReviewById(Long reviewId, Long userId);
    Page<CodeReviewRequestDTO> getMyCodeReviews(Long userId, Pageable pageable);
    void deleteCodeReview(Long reviewId, Long userId);
    long getTotalReviewsCount();
}