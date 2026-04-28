package com.buildwithus.project.dto;

import com.buildwithus.common.enums.CollaborationStatus;
import com.buildwithus.common.enums.ProjectCategory;
import com.buildwithus.common.enums.ProjectStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSearchRequest {
    private String keyword;
    private ProjectCategory category;
    private ProjectStage projectStage;
    private CollaborationStatus collaborationStatus;
}