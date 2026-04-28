package com.buildwithus.notification.service;

import com.buildwithus.common.enums.BadgeType;
import com.buildwithus.notification.dto.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable);
    int getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId, Long userId);
    
    // Notification senders
    void sendFollowNotification(Long followerId, Long followingId);
    void sendCollaborationRequestNotification(Long ownerId, Long requesterId, Long projectId);
    void sendCollaborationAcceptedNotification(Long requesterId, Long projectId);
    void sendCollaborationRejectedNotification(Long requesterId, Long projectId);
    void sendVerificationApprovedNotification(Long userId);
    void sendVerificationRejectedNotification(Long userId);
    void sendTeamJoinRequestNotification(Long postOwnerId, Long requesterId, Long postId);
    void sendTeamJoinAcceptedNotification(Long requesterId, Long postId);
    void sendTeamJoinRejectedNotification(Long requesterId, Long postId);
    void sendBadgeAwardedNotification(Long userId, BadgeType badgeType);
}