package com.buildwithus.project.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.CollaborationStatus;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.ProjectCategory;
import com.buildwithus.common.enums.ProjectStage;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;

    @Enumerated(EnumType.STRING)
    private ProjectCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_stage")
    private ProjectStage projectStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "collaboration_status")
    private CollaborationStatus collaborationStatus;

    @Column(name = "collaborators_needed")
    private Integer collaboratorsNeeded;

    @Column(name = "is_visible")
    private Boolean isVisible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Project Links
    @Column(name = "github_repo_url")
    private String githubRepoUrl;

    @Column(name = "live_demo_url")
    private String liveDemoUrl;

    @Column(name = "documentation_url")
    private String documentationUrl;

    @Column(name = "video_demo_url")
    private String videoDemoUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    @ElementCollection
    @CollectionTable(name = "project_tech_stacks", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech_stack")
    @Builder.Default
    private Set<String> techStack = new HashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "project_roles_needed", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "role_needed")
    @Builder.Default
    private Set<PrimaryRole> rolesNeeded = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProjectImage> images = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<ProjectCollaborator> collaborators = new HashSet<>();

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    public void addImage(ProjectImage image) {
        images.add(image);
        image.setProject(this);
    }

    public void removeImage(ProjectImage image) {
        images.remove(image);
        image.setProject(null);
    }
}