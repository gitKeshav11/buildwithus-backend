package com.buildwithus.notification.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.NotificationType;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(name = "reference_type")
    private String referenceType;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
}