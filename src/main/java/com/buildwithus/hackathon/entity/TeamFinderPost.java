package com.buildwithus.hackathon.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.TeamFinderType;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "team_finder_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamFinderPost extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private TeamFinderType postType;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "team_finder_roles_needed", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "role_needed")
    @Builder.Default
    private Set<PrimaryRole> rolesNeeded = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "team_finder_skills_required", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "skill")
    @Builder.Default
    private Set<String> skillsRequired = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_experience")
    private ExperienceLevel preferredExperience;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}