package com.buildwithus.notification.service.impl;

import com.buildwithus.common.enums.BadgeType;
import com.buildwithus.common.enums.NotificationType;
import com.buildwithus.exception.ForbiddenException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.notification.dto.NotificationDTO;
import com.buildwithus.notification.entity.Notification;
import com.buildwithus.notification.repository.NotificationRepository;
import com.buildwithus.notification.service.NotificationService;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable).map(this::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to modify this notification");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }
    
    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this notification");
        }
        
        notification.setIsDeleted(true);
        notificationRepository.save(notification);
    }
    
    @Override
    public void sendFollowNotification(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElse(null);
        if (follower == null) return;
        
        createNotification(
                followingId,
                NotificationType.NEW_FOLLOWER,
                "New Follower",
                follower.getFullName() + " started following you",
                followerId,
                "USER"
        );
    }
    
    @Override
    public void sendCollaborationRequestNotification(Long ownerId, Long requesterId, Long projectId) {
        User requester = userRepository.findById(requesterId).orElse(null);
        if (requester == null) return;
        
        createNotification(
                ownerId,
                NotificationType.COLLABORATION_REQUEST,
                "Collaboration Request",
                requester.getFullName() + " wants to collaborate on your project",
                projectId,
                "PROJECT"
        );
    }
    
    @Override
    public void sendCollaborationAcceptedNotification(Long requesterId, Long projectId) {
        createNotification(
                requesterId,
                NotificationType.COLLABORATION_ACCEPTED,
                "Collaboration Accepted",
                "Your collaboration request has been accepted!",
                projectId,
                "PROJECT"
        );
    }
    
    @Override
    public void sendCollaborationRejectedNotification(Long requesterId, Long projectId) {
        createNotification(
                requesterId,
                NotificationType.COLLABORATION_REJECTED,
                "Collaboration Request Update",
                "Your collaboration request was not accepted",
                projectId,
                "PROJECT"
        );
    }
    
    @Override
    public void sendVerificationApprovedNotification(Long userId) {
        createNotification(
                userId,
                NotificationType.VERIFICATION_APPROVED,
                "Verification Approved! 🎉",
                "Congratulations! You are now a verified developer.",
                null,
                null
        );
    }
    
    @Override
    public void sendVerificationRejectedNotification(Long userId) {
        createNotification(
                userId,
                NotificationType.VERIFICATION_REJECTED,
                "Verification Update",
                "Your verification request was not approved. Please check the requirements and try again.",
                null,
                null
        );
    }
    
    @Override
    public void sendTeamJoinRequestNotification(Long postOwnerId, Long requesterId, Long postId) {
        User requester = userRepository.findById(requesterId).orElse(null);
        if (requester == null) return;
        
        createNotification(
                postOwnerId,
                NotificationType.TEAM_JOIN_REQUEST,
                "Team Join Request",
                requester.getFullName() + " wants to join your team",
                postId,
                "TEAM_FINDER_POST"
        );
    }
    
    @Override
    public void sendTeamJoinAcceptedNotification(Long requesterId, Long postId) {
        createNotification(
                requesterId,
                NotificationType.TEAM_JOIN_ACCEPTED,
                "Team Join Accepted",
                "Your request to join the team has been accepted!",
                postId,
                "TEAM_FINDER_POST"
        );
    }
    
    @Override
    public void sendTeamJoinRejectedNotification(Long requesterId, Long postId) {
        createNotification(
                requesterId,
                NotificationType.TEAM_JOIN_REJECTED,
                "Team Join Update",
                "Your request to join the team was not accepted",
                postId,
                "TEAM_FINDER_POST"
        );
    }
    
    @Override
    public void sendBadgeAwardedNotification(Long userId, BadgeType badgeType) {
        createNotification(
                userId,
                NotificationType.NEW_BADGE,
                "New Badge Earned! 🏆",
                "You've earned the " + formatBadgeName(badgeType) + " badge!",
                null,
                "BADGE"
        );
    }
    
    private void createNotification(Long userId, NotificationType type, String title, 
                                    String message, Long referenceId, String referenceType) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
    }
    
    private String formatBadgeName(BadgeType badgeType) {
        return badgeType.name().replace("_", " ").toLowerCase();
    }
    
    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}