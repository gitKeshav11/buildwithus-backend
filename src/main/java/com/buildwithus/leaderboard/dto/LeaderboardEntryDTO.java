package com.buildwithus.leaderboard.dto;

import com.buildwithus.common.enums.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String profilePhotoUrl;
    private String headline;
    private Integer totalPoints;
    private Integer rank;
    private List<BadgeType> badges;
    private Boolean isVerified;
}