package com.buildwithus.admin.controller;

import com.buildwithus.admin.dto.AdminStatsDTO;
import com.buildwithus.ai.service.ChatService;
import com.buildwithus.ai.service.CodeReviewService;
import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.job.service.JobService;
import com.buildwithus.project.service.ProjectService;
import com.buildwithus.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserService userService;
    private final JobService jobService;
    private final ProjectService projectService;
    private final CodeReviewService codeReviewService;
    private final ChatService chatService;
    
    @GetMapping("/stats")
    @Operation(summary = "Get platform statistics")
    public ResponseEntity<ApiResponse<AdminStatsDTO>> getStats() {
        AdminStatsDTO stats = AdminStatsDTO.builder()
                .totalUsers(userService.getTotalUsersCount())
                .totalJobs(jobService.getTotalJobsCount())
                .totalProjects(projectService.getTotalProjectsCount())
                .totalCollaborations(projectService.getTotalCollaborationsCount())
                .totalCodeReviews(codeReviewService.getTotalReviewsCount())
                .totalChatSessions(chatService.getTotalConversationsCount())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}