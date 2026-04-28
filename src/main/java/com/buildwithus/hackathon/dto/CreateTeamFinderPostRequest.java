package com.buildwithus.hackathon.dto;

import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.TeamFinderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamFinderPostRequest {
    
    private Long hackathonId;
    
    @NotNull(message = "Post type is required")
    private TeamFinderType postType;
    
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @Size(max = 2000, message = "Message must be less than 2000 characters")
    private String message;
    
    private Set<PrimaryRole> rolesNeeded;
    
    private Set<String> skillsRequired;
    
    private ExperienceLevel preferredExperience;
}