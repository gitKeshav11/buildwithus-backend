package com.buildwithus.leaderboard.dto;

import com.buildwithus.common.enums.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadgeDTO {
    private Long id;
    private BadgeType badgeType;
    private String awardedReason;
    private LocalDateTime awardedAt;
}