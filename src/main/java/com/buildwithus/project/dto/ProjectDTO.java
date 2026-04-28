package com.buildwithus.project.dto;

import com.buildwithus.common.enums.CollaborationStatus;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.ProjectCategory;
import com.buildwithus.common.enums.ProjectStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String title;
    private String slug;
    private String shortDescription;
    private String detailedDescription;
    private ProjectCategory category;
    private ProjectStage projectStage;
    private CollaborationStatus collaborationStatus;
    private Integer collaboratorsNeeded;
    private Boolean isVisible;
    private Long ownerId;
    private String ownerName;
    private String ownerUsername;
    private String ownerProfilePhotoUrl;
    private String githubRepoUrl;
    private String liveDemoUrl;
    private String documentationUrl;
    private String videoDemoUrl;
    private String websiteUrl;
    private Set<String> techStack;
    private Set<PrimaryRole> rolesNeeded;
    private List<ProjectImageDTO> images;
    private List<CollaboratorDTO> collaborators;
    private Integer viewsCount;
    private Integer collaboratorsCount;
    private Boolean isCollaborator;
    private Boolean hasRequestedCollaboration;
    private LocalDateTime createdAt;
}