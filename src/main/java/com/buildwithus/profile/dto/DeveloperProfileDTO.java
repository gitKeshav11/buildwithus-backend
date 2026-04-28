package com.buildwithus.profile.dto;

import com.buildwithus.common.enums.AvailabilityStatus;
import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
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
public class DeveloperProfileDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String username;
    private String email;
    private String headline;
    private String bio;
    private String location;
    private String collegeOrCompany;
    private ExperienceLevel experienceLevel;
    private AvailabilityStatus availabilityStatus;
    private PrimaryRole primaryRole;
    private Integer yearsOfExperience;
    private String profilePhotoUrl;
    private String coverPhotoUrl;
    private String githubUrl;
    private String linkedinUrl;
    private String portfolioUrl;
    private String twitterUrl;
    private String leetcodeUrl;
    private String codeforcesUrl;
    private String resumeUrl;
    private Boolean profileVisibility;
    private Integer profileCompletionPercentage;
    private Boolean isVerified;
    private Set<SkillDTO> skills;
    private Set<TechStackDTO> techStacks;
    private Integer followersCount;
    private Integer followingCount;
    private Boolean isFollowing;
    private LocalDateTime createdAt;
}