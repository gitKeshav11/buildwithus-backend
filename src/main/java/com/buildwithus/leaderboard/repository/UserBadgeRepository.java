package com.buildwithus.leaderboard.repository;

import com.buildwithus.common.enums.BadgeType;
import com.buildwithus.leaderboard.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUserId(Long userId);
    Optional<UserBadge> findByUserIdAndBadgeType(Long userId, BadgeType badgeType);
    boolean existsByUserIdAndBadgeType(Long userId, BadgeType badgeType);
}