package com.buildwithus.hackathon.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hackathons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hackathon extends BaseEntity {
    
    @Column(nullable = false)
    private String title;
    
    private String organizer;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "registration_link")
    private String registrationLink;
    
    @Column(name = "event_date")
    private LocalDate eventDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    private String location;
    
    @Column(name = "is_online")
    private Boolean isOnline = false;
    
    @Column(name = "team_size")
    private Integer teamSize;
    
    @Column(name = "prize_info", columnDefinition = "TEXT")
    private String prizeInfo;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by")
    private User postedBy;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "hackathon_required_roles", joinColumns = @JoinColumn(name = "hackathon_id"))
    @Column(name = "required_role")
    @Builder.Default
    private Set<PrimaryRole> requiredRoles = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "hackathon_tags", joinColumns = @JoinColumn(name = "hackathon_id"))
    @Column(name = "tag")
    @Builder.Default
    private Set<String> tags = new HashSet<>();
}