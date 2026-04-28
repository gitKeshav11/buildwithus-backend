package com.buildwithus.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {
    private long totalUsers;
    private long totalJobs;
    private long totalProjects;
    private long totalCollaborations;
    private long totalCodeReviews;
    private long totalChatSessions;
}