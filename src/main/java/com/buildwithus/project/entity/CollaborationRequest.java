package com.buildwithus.project.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.RequestStatus;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "collaboration_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationRequest extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_role")
    private PrimaryRole requestedRole;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;
}