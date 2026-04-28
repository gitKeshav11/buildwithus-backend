package com.buildwithus.job.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.JobType;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.WorkMode;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "company_logo_url")
    private String companyLogoUrl;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role_category")
    private PrimaryRole roleCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode")
    private WorkMode workMode;
    
    private String location;
    
    @Column(name = "salary_range")
    private String salaryRange;
    
    @Column(name = "apply_link", nullable = false)
    private String applyLink;
    
    @Column(name = "last_date")
    private LocalDate lastDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by")
    private User postedBy;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @ElementCollection
    @CollectionTable(name = "job_tags", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "tag")
    @Builder.Default
    private Set<String> tags = new HashSet<>();
    
    @Column(name = "views_count")
    private Integer viewsCount = 0;
    
    @Column(name = "clicks_count")
    private Integer clicksCount = 0;
}