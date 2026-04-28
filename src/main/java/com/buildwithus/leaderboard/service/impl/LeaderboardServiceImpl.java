package com.buildwithus.leaderboard.service.impl;

import com.buildwithus.ai.repository.ChatConversationRepository;
import com.buildwithus.ai.repository.CodeReviewRequestRepository;
import com.buildwithus.common.enums.BadgeType;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.follow.repository.FollowRepository;
import com.buildwithus.leaderboard.dto.LeaderboardEntryDTO;
import com.buildwithus.leaderboard.dto.UserBadgeDTO;
import com.buildwithus.leaderboard.entity.LeaderboardPoints;
import com.buildwithus.leaderboard.entity.UserBadge;
import com.buildwithus.leaderboard.repository.LeaderboardPointsRepository;
import com.buildwithus.leaderboard.repository.UserBadgeRepository;
import com.buildwithus.leaderboard.service.LeaderboardService;
import com.buildwithus.notification.service.NotificationService;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
import com.buildwithus.project.repository.CollaborationRequestRepository;
import com.buildwithus.project.repository.ProjectRepository;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LeaderboardServiceImpl implements LeaderboardService {
    
    private final LeaderboardPointsRepository pointsRepository;
    private final UserBadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final DeveloperProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final CollaborationRequestRepository collaborationRepository;
    private final FollowRepository followRepository;
    private final CodeReviewRequestRepository codeReviewRepository;
    private final ChatConversationRepository chatRepository;
    private final NotificationService notificationService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<LeaderboardEntryDTO> getTopDevelopers(Pageable pageable) {
        return pointsRepository.findTopDevelopers(pageable).map(this::toLeaderboardEntry);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LeaderboardEntryDTO getUserLeaderboardEntry(Long userId) {
        LeaderboardPoints points = pointsRepository.findByUserId(userId)
                .orElseGet(() -> createInitialPoints(userId));
        return toLeaderboardEntry(points);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getUserRank(Long userId) {
        return pointsRepository.getUserRank(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserBadgeDTO> getUserBadges(Long userId) {
        return badgeRepository.findByUserId(userId).stream()
                .map(this::toBadgeDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void awardBadge(Long userId, BadgeType badgeType, String reason) {
        if (badgeRepository.existsByUserIdAndBadgeType(userId, badgeType)) {
            return; // Already has this badge
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        UserBadge badge = UserBadge.builder()
                .user(user)
                .badgeType(badgeType)
                .awardedReason(reason)
                .build();
        badgeRepository.save(badge);
        
        // Send notification
        notificationService.sendBadgeAwardedNotification(userId, badgeType);
    }
    
    @Override
    public void recalculateUserPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        LeaderboardPoints points = pointsRepository.findByUserId(userId)
                .orElseGet(() -> createInitialPoints(userId));
        
        DeveloperProfile profile = profileRepository.findByUserId(userId).orElse(null);
        
        // Profile completion points (max 20)
        int profilePoints = profile != null ? (profile.getProfileCompletionPercentage() / 5) : 0;
        points.setProfileCompletionPoints(Math.min(profilePoints, 20));
        
        // Projects points (5 per project, max 50)
        long projectsCount = projectRepository.findByOwnerId(userId, Pageable.unpaged()).getTotalElements();
        points.setProjectsPoints((int) Math.min(projectsCount * 5, 50));
        
        // Collaborations points (10 per accepted collaboration, max 50)
        // This is simplified - you'd want to track accepted collaborations
        points.setCollaborationsPoints(0);
        
        // Followers points (1 per follower, max 100)
        int followersCount = followRepository.countByFollowingId(userId);
        points.setFollowersPoints(Math.min(followersCount, 100));
        
        // Code reviews points (3 per review, max 30)
        long reviewsCount = codeReviewRepository.findByUserId(userId, Pageable.unpaged()).getTotalElements();
        points.setCodeReviewsPoints((int) Math.min(reviewsCount * 3, 30));
        
        // AI engagement points (2 per conversation, max 20)
        long conversationsCount = chatRepository.findByUserId(userId, Pageable.unpaged()).getTotalElements();
        points.setAiEngagementPoints((int) Math.min(conversationsCount * 2, 20));
        
        // Verification points (50 if verified)
        if (profile != null && profile.getIsVerified()) {
            points.setVerificationPoints(50);
        } else {
            points.setVerificationPoints(0);
        }
        
        points.recalculateTotal();
        pointsRepository.save(points);
        
        // Check for badge eligibility
        checkAndAwardBadges(userId, points, profile);
    }
    
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM daily
    public void recalculateAllPoints() {
        log.info("Starting daily leaderboard recalculation");
        userRepository.findAll().forEach(user -> {
            try {
                recalculateUserPoints(user.getId());
            } catch (Exception e) {
                log.error("Error recalculating points for user {}", user.getId(), e);
            }
        });
        log.info("Completed daily leaderboard recalculation");
    }
    
    private LeaderboardPoints createInitialPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        LeaderboardPoints points = LeaderboardPoints.builder()
                .user(user)
                .totalPoints(0)
                .build();
        return pointsRepository.save(points);
    }
    
    private void checkAndAwardBadges(Long userId, LeaderboardPoints points, DeveloperProfile profile) {
        // Verified Developer badge
        if (profile != null && profile.getIsVerified()) {
            awardBadge(userId, BadgeType.VERIFIED_DEVELOPER, "Developer verification approved");
        }
        
        // Profile Complete badge
        if (profile != null && profile.getProfileCompletionPercentage() >= 90) {
            awardBadge(userId, BadgeType.PROFILE_COMPLETE, "Profile is 90% or more complete");
        }
        
        // First Project badge
        if (points.getProjectsPoints() > 0) {
            awardBadge(userId, BadgeType.FIRST_PROJECT, "Posted first project");
        }
        
        // Project Builder badge (5+ projects)
        if (points.getProjectsPoints() >= 25) {
            awardBadge(userId, BadgeType.PROJECT_BUILDER, "Posted 5 or more projects");
        }
        
        // AI Explorer badge
        if (points.getAiEngagementPoints() >= 10) {
            awardBadge(userId, BadgeType.AI_EXPLORER, "Active AI feature user");
        }
        
        // Community Rising badge (20+ followers)
        if (points.getFollowersPoints() >= 20) {
            awardBadge(userId, BadgeType.COMMUNITY_RISING, "Gained 20 or more followers");
        }
        
        // Top Contributor badge (200+ total points)
        if (points.getTotalPoints() >= 200) {
            awardBadge(userId, BadgeType.TOP_CONTRIBUTOR, "Reached 200+ total points");
        }
    }
    
    private LeaderboardEntryDTO toLeaderboardEntry(LeaderboardPoints points) {
        DeveloperProfile profile = profileRepository.findByUserId(points.getUser().getId()).orElse(null);
        List<BadgeType> badges = badgeRepository.findByUserId(points.getUser().getId()).stream()
                .map(UserBadge::getBadgeType)
                .collect(Collectors.toList());
        
        int rank = pointsRepository.getUserRank(points.getUser().getId());
        
        return LeaderboardEntryDTO.builder()
                .userId(points.getUser().getId())
                .username(points.getUser().getUsername())
                .fullName(points.getUser().getFullName())
                .profilePhotoUrl(profile != null ? profile.getProfilePhotoUrl() : null)
                .headline(profile != null ? profile.getHeadline() : null)
                .totalPoints(points.getTotalPoints())
                .rank(rank)
                .badges(badges)
                .isVerified(profile != null ? profile.getIsVerified() : false)
                .build();
    }
    
    private UserBadgeDTO toBadgeDTO(UserBadge badge) {
        return UserBadgeDTO.builder()
                .id(badge.getId())
                .badgeType(badge.getBadgeType())
                .awardedReason(badge.getAwardedReason())
                .awardedAt(badge.getCreatedAt())
                .build();
    }
}