package com.buildwithus.hackathon.dto;

import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.TeamFinderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamFinderPostDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String profilePhotoUrl;
    private Long hackathonId;
    private String hackathonTitle;
    private TeamFinderType postType;
    private String title;
    private String message;
    private Set<PrimaryRole> rolesNeeded;
    private Set<String> skillsRequired;
    private ExperienceLevel preferredExperience;
    private Boolean isActive;
    private LocalDateTime createdAt;
}