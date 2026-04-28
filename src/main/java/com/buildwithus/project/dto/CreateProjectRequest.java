package com.buildwithus.project.dto;

import com.buildwithus.common.enums.CollaborationStatus;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.ProjectCategory;
import com.buildwithus.common.enums.ProjectStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @Size(max = 500, message = "Short description must be less than 500 characters")
    private String shortDescription;
    
    @Size(max = 10000, message = "Detailed description must be less than 10000 characters")
    private String detailedDescription;
    
    private ProjectCategory category;
    
    private ProjectStage projectStage;
    
    private CollaborationStatus collaborationStatus;
    
    private Integer collaboratorsNeeded;
    
    private Boolean isVisible;
    
    @URL(message = "Invalid GitHub URL")
    private String githubRepoUrl;
    
    @URL(message = "Invalid live demo URL")
    private String liveDemoUrl;
    
    @URL(message = "Invalid documentation URL")
    private String documentationUrl;
    
    @URL(message = "Invalid video demo URL")
    private String videoDemoUrl;
    
    @URL(message = "Invalid website URL")
    private String websiteUrl;
    
    private Set<String> techStack;
    
    private Set<PrimaryRole> rolesNeeded;
}