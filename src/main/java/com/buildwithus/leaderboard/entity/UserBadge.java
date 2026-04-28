package com.buildwithus.leaderboard.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.BadgeType;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_badges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "badge_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private BadgeType badgeType;
    
    @Column(name = "awarded_reason")
    private String awardedReason;
}