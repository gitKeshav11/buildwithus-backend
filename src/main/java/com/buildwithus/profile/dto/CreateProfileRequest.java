package com.buildwithus.profile.dto;

import com.buildwithus.common.enums.AvailabilityStatus;
import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
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
public class CreateProfileRequest {
    
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;
    
    @Size(max = 200, message = "Headline must be less than 200 characters")
    private String headline;
    
    @Size(max = 2000, message = "Bio must be less than 2000 characters")
    private String bio;
    
    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;
    
    @Size(max = 200, message = "College/Company must be less than 200 characters")
    private String collegeOrCompany;
    
    private ExperienceLevel experienceLevel;
    
    private AvailabilityStatus availabilityStatus;
    
    private PrimaryRole primaryRole;
    
    private Integer yearsOfExperience;
    
    @URL(message = "Invalid GitHub URL")
    private String githubUrl;
    
    @URL(message = "Invalid LinkedIn URL")
    private String linkedinUrl;
    
    @URL(message = "Invalid Portfolio URL")
    private String portfolioUrl;
    
    @URL(message = "Invalid Twitter URL")
    private String twitterUrl;
    
    @URL(message = "Invalid LeetCode URL")
    private String leetcodeUrl;
    
    @URL(message = "Invalid Codeforces URL")
    private String codeforcesUrl;
    
    @URL(message = "Invalid Resume URL")
    private String resumeUrl;
    
    private Boolean profileVisibility;
    
    private Set<String> skills;
    
    private Set<String> techStacks;
}