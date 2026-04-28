package com.buildwithus.job.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.common.enums.JobType;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.WorkMode;
import com.buildwithus.job.dto.*;
import com.buildwithus.job.service.JobService;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs & Internships", description = "Job posting and management APIs")
public class JobController {
    
    private final JobService jobService;
    
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    @Operation(summary = "Create a new job posting")
    public ResponseEntity<ApiResponse<JobDTO>> createJob(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateJobRequest request) {
        JobDTO job = jobService.createJob(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created successfully", job));
    }
    
    @PutMapping("/{jobId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a job posting")
    public ResponseEntity<ApiResponse<JobDTO>> updateJob(
            @PathVariable Long jobId,
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody UpdateJobRequest request) {
        JobDTO job = jobService.updateJob(jobId, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", job));
    }
    
    @DeleteMapping("/{jobId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a job posting")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long jobId,
            @CurrentUser UserPrincipal currentUser) {
        jobService.deleteJob(jobId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully"));
    }
    
    @GetMapping("/{jobId}")
    @Operation(summary = "Get job by ID")
    public ResponseEntity<ApiResponse<JobDTO>> getJobById(
            @PathVariable Long jobId,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        JobDTO job = jobService.getJobById(jobId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(job));
    }
    
    @GetMapping
    @Operation(summary = "Get all jobs")
    public ResponseEntity<ApiResponse<PagedResponse<JobDTO>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var jobs = jobService.getAllJobs(PageRequest.of(page, size, sort), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(jobs)));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search jobs")
    public ResponseEntity<ApiResponse<PagedResponse<JobDTO>>> searchJobs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var jobs = jobService.searchJobs(keyword, PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(jobs)));
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filter jobs")
    public ResponseEntity<ApiResponse<PagedResponse<JobDTO>>> filterJobs(
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) PrimaryRole roleCategory,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        JobSearchRequest request = JobSearchRequest.builder()
                .jobType(jobType)
                .roleCategory(roleCategory)
                .workMode(workMode)
                .location(location)
                .build();
        var jobs = jobService.filterJobs(request, PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(jobs)));
    }
    
    @GetMapping("/featured")
    @Operation(summary = "Get featured jobs")
    public ResponseEntity<ApiResponse<PagedResponse<JobDTO>>> getFeaturedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var jobs = jobService.getFeaturedJobs(PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(jobs)));
    }
    
    @GetMapping("/my-posts")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my posted jobs")
    public ResponseEntity<ApiResponse<PagedResponse<JobDTO>>> getMyPostedJobs(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var jobs = jobService.getMyPostedJobs(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(jobs)));
    }
    
    @PostMapping("/{jobId}/save")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Save a job")
    public ResponseEntity<ApiResponse<Void>> saveJob(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long jobId) {
        jobService.saveJob(currentUser.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.success("Job saved successfully"));
    }
    
    @DeleteMapping("/{jobId}/save")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Unsave a job")
    public ResponseEntity<ApiResponse<Void>> unsaveJob(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long jobId) {
        jobService.unsaveJob(currentUser.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.success("Job unsaved successfully"));
    }
    
    @GetMapping("/saved")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get saved jobs")
    public ResponseEntity<ApiResponse<PagedResponse<JobDTO>>> getSavedJobs(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var jobs = jobService.getSavedJobs(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(jobs)));
    }
    
    @PostMapping("/{jobId}/click")
    @Operation(summary = "Track job click")
    public ResponseEntity<ApiResponse<Void>> trackJobClick(@PathVariable Long jobId) {
        jobService.trackJobClick(jobId);
        return ResponseEntity.ok(ApiResponse.success("Click tracked"));
    }
}