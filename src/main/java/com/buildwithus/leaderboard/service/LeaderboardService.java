package com.buildwithus.leaderboard.service;

import com.buildwithus.common.enums.BadgeType;
import com.buildwithus.leaderboard.dto.LeaderboardEntryDTO;
import com.buildwithus.leaderboard.dto.UserBadgeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaderboardService {
    Page<LeaderboardEntryDTO> getTopDevelopers(Pageable pageable);
    LeaderboardEntryDTO getUserLeaderboardEntry(Long userId);
    int getUserRank(Long userId);
    List<UserBadgeDTO> getUserBadges(Long userId);
    void awardBadge(Long userId, BadgeType badgeType, String reason);
    void recalculateUserPoints(Long userId);
    void recalculateAllPoints();
}