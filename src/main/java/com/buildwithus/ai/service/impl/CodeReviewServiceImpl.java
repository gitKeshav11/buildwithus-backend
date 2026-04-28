package com.buildwithus.ai.service.impl;

import com.buildwithus.ai.dto.CodeReviewRequestDTO;
import com.buildwithus.ai.dto.CreateCodeReviewRequest;
import com.buildwithus.ai.entity.CodeReviewRequest;
import com.buildwithus.ai.repository.CodeReviewRequestRepository;
import com.buildwithus.ai.service.AiService;
import com.buildwithus.ai.service.CodeReviewService;
import com.buildwithus.exception.ForbiddenException;
import com.buildwithus.exception.RateLimitExceededException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CodeReviewServiceImpl implements CodeReviewService {

    private final CodeReviewRequestRepository reviewRepository;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.rate-limit.ai-requests-per-hour:20}")
    private int aiRequestsPerHour;

    @Override
    public CodeReviewRequestDTO submitCodeReview(Long userId, CreateCodeReviewRequest request) {
        checkRateLimit(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        CodeReviewRequestDTO aiResult = aiService.performCodeReview(
                request.getCodeContent(),
                request.getProgrammingLanguage(),
                request.getContextDescription()
        );

        CodeReviewRequest review = CodeReviewRequest.builder()
                .user(user)
                .title(request.getTitle())
                .codeContent(request.getCodeContent())
                .programmingLanguage(request.getProgrammingLanguage())
                .contextDescription(request.getContextDescription())
                .reviewSummary(aiResult.getReviewSummary())
                .detectedIssues(aiResult.getDetectedIssues())
                .improvementSuggestions(aiResult.getImprovementSuggestions())
                .securityObservations(aiResult.getSecurityObservations())
                .optimizationSuggestions(aiResult.getOptimizationSuggestions())
                .improvedCode(aiResult.getImprovedCode())
                .qualityScore(aiResult.getQualityScore())
                .isProcessed(true)
                .build();

        review = reviewRepository.save(review);

        incrementRateLimit(userId);

        return toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public CodeReviewRequestDTO getCodeReviewById(Long reviewId, Long userId) {
        CodeReviewRequest review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("CodeReviewRequest", "id", reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to view this review");
        }

        return toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CodeReviewRequestDTO> getMyCodeReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable).map(this::toDTO);
    }

    @Override
    public void deleteCodeReview(Long reviewId, Long userId) {
        CodeReviewRequest review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("CodeReviewRequest", "id", reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this review");
        }

        review.setIsDeleted(true);
        reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalReviewsCount() {
        return reviewRepository.countAllReviews();
    }

    private void checkRateLimit(Long userId) {
        try {
            String key = "rate_limit:code_review:" + userId;
            Object value = redisTemplate.opsForValue().get(key);

            Integer count = value instanceof Integer
                    ? (Integer) value
                    : value != null ? Integer.parseInt(value.toString()) : null;

            if (count != null && count >= aiRequestsPerHour) {
                throw new RateLimitExceededException(
                        "You have exceeded the maximum number of code reviews per hour. Please try again later."
                );
            }
        } catch (RateLimitExceededException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Redis rate-limit check failed. Continuing without blocking.", e);
        }
    }

    private void incrementRateLimit(Long userId) {
        try {
            String key = "rate_limit:code_review:" + userId;
            redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, Duration.ofHours(1));
        } catch (Exception e) {
            log.warn("Redis rate-limit increment failed", e);
        }
    }

    private CodeReviewRequestDTO toDTO(CodeReviewRequest review) {
        return CodeReviewRequestDTO.builder()
                .id(review.getId())
                .title(review.getTitle())
                .codeContent(review.getCodeContent())
                .programmingLanguage(review.getProgrammingLanguage())
                .contextDescription(review.getContextDescription())
                .reviewSummary(review.getReviewSummary())
                .detectedIssues(review.getDetectedIssues())
                .improvementSuggestions(review.getImprovementSuggestions())
                .securityObservations(review.getSecurityObservations())
                .optimizationSuggestions(review.getOptimizationSuggestions())
                .improvedCode(review.getImprovedCode())
                .qualityScore(review.getQualityScore())
                .isProcessed(review.getIsProcessed())
                .createdAt(review.getCreatedAt())
                .build();
    }
}