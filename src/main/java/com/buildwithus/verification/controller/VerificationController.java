package com.buildwithus.verification.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import com.buildwithus.verification.dto.VerificationRequestDTO;
import com.buildwithus.verification.dto.VerificationReviewRequest;
import com.buildwithus.verification.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
@Tag(name = "Developer Verification", description = "Developer verification badge APIs")
@SecurityRequirement(name = "bearerAuth")
public class VerificationController {
    
    private final VerificationService verificationService;
    
    @PostMapping("/request")
    @Operation(summary = "Request developer verification")
    public ResponseEntity<ApiResponse<VerificationRequestDTO>> requestVerification(
            @CurrentUser UserPrincipal currentUser) {
        VerificationRequestDTO result = verificationService.requestVerification(currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Verification request submitted", result));
    }
    
    @GetMapping("/status")
    @Operation(summary = "Get my verification status")
    public ResponseEntity<ApiResponse<VerificationRequestDTO>> getMyVerificationStatus(
            @CurrentUser UserPrincipal currentUser) {
        VerificationRequestDTO result = verificationService.getMyVerificationStatus(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending verification requests (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<VerificationRequestDTO>>> getPendingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var requests = verificationService.getPendingVerificationRequests(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(requests)));
    }
    
    @PostMapping("/{requestId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Review verification request (Admin only)")
    public ResponseEntity<ApiResponse<VerificationRequestDTO>> reviewVerificationRequest(
            @PathVariable Long requestId,
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody VerificationReviewRequest request) {
        VerificationRequestDTO result = verificationService.reviewVerificationRequest(
                requestId, currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Verification request reviewed", result));
    }
}