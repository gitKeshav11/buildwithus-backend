package com.buildwithus.ai.controller;

import com.buildwithus.ai.dto.CodeReviewRequestDTO;
import com.buildwithus.ai.dto.CreateCodeReviewRequest;
import com.buildwithus.ai.service.CodeReviewService;
import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/code-review")
@RequiredArgsConstructor
@Tag(name = "AI Code Review", description = "AI-powered code review APIs")
@SecurityRequirement(name = "bearerAuth")
public class CodeReviewController {
    
    private final CodeReviewService codeReviewService;
    
    @PostMapping
    @Operation(summary = "Submit code for AI review")
    public ResponseEntity<ApiResponse<CodeReviewRequestDTO>> submitCodeReview(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateCodeReviewRequest request) {
        CodeReviewRequestDTO result = codeReviewService.submitCodeReview(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Code review completed", result));
    }
    
    @GetMapping("/{reviewId}")
    @Operation(summary = "Get code review by ID")
    public ResponseEntity<ApiResponse<CodeReviewRequestDTO>> getCodeReviewById(
            @PathVariable Long reviewId,
            @CurrentUser UserPrincipal currentUser) {
        CodeReviewRequestDTO result = codeReviewService.getCodeReviewById(reviewId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @GetMapping
    @Operation(summary = "Get my code reviews")
    public ResponseEntity<ApiResponse<PagedResponse<CodeReviewRequestDTO>>> getMyCodeReviews(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var reviews = codeReviewService.getMyCodeReviews(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(reviews)));
    }
    
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a code review")
    public ResponseEntity<ApiResponse<Void>> deleteCodeReview(
            @PathVariable Long reviewId,
            @CurrentUser UserPrincipal currentUser) {
        codeReviewService.deleteCodeReview(reviewId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Code review deleted"));
    }
}