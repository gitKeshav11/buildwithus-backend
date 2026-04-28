package com.buildwithus.leaderboard.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leaderboard_points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardPoints extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "total_points")
    private Integer totalPoints = 0;
    
    @Column(name = "profile_completion_points")
    private Integer profileCompletionPoints = 0;
    
    @Column(name = "projects_points")
    private Integer projectsPoints = 0;
    
    @Column(name = "collaborations_points")
    private Integer collaborationsPoints = 0;
    
    @Column(name = "followers_points")
    private Integer followersPoints = 0;
    
    @Column(name = "code_reviews_points")
    private Integer codeReviewsPoints = 0;
    
    @Column(name = "ai_engagement_points")
    private Integer aiEngagementPoints = 0;
    
    @Column(name = "hackathon_points")
    private Integer hackathonPoints = 0;
    
    @Column(name = "verification_points")
    private Integer verificationPoints = 0;
    
    public void recalculateTotal() {
        this.totalPoints = profileCompletionPoints + projectsPoints + collaborationsPoints +
                followersPoints + codeReviewsPoints + aiEngagementPoints + hackathonPoints + verificationPoints;
    }
}