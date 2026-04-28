package com.buildwithus.profile.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.AvailabilityStatus;
import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "developer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeveloperProfile extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(unique = true)
    private String username;
    
    @Column(unique = true)
    private String email;
    
    private String headline;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    private String location;
    
    @Column(name = "college_or_company")
    private String collegeOrCompany;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    private AvailabilityStatus availabilityStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_role")
    private PrimaryRole primaryRole;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;
    
    @Column(name = "cover_photo_url")
    private String coverPhotoUrl;
    
    @Column(name = "github_url")
    private String githubUrl;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "portfolio_url")
    private String portfolioUrl;
    
    @Column(name = "twitter_url")
    private String twitterUrl;
    
    @Column(name = "leetcode_url")
    private String leetcodeUrl;
    
    @Column(name = "codeforces_url")
    private String codeforcesUrl;
    
    @Column(name = "resume_url")
    private String resumeUrl;
    
    @Column(name = "profile_visibility")
    private Boolean profileVisibility = true;
    
    @Column(name = "profile_completion_percentage")
    private Integer profileCompletionPercentage = 0;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "profile_skills",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "profile_tech_stacks",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "tech_stack_id")
    )
    @Builder.Default
    private Set<TechStack> techStacks = new HashSet<>();
}
