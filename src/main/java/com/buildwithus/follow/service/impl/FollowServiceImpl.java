package com.buildwithus.follow.service.impl;

import com.buildwithus.exception.BadRequestException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.follow.dto.FollowDTO;
import com.buildwithus.follow.dto.FollowStatsDTO;
import com.buildwithus.follow.entity.Follow;
import com.buildwithus.follow.repository.FollowRepository;
import com.buildwithus.follow.service.FollowService;
import com.buildwithus.notification.service.NotificationService;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
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
public class FollowServiceImpl implements FollowService {
    
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final DeveloperProfileRepository profileRepository;
    private final NotificationService notificationService;
    
    @Override
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BadRequestException("You cannot follow yourself");
        }
        
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BadRequestException("You are already following this user");
        }
        
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followerId));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", followingId));
        
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();
        
        followRepository.save(follow);
        
        // Send notification
        notificationService.sendFollowNotification(followerId, followingId);
    }
    
    @Override
    public void unfollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BadRequestException("You cannot unfollow yourself");
        }
        
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new BadRequestException("You are not following this user"));
        
        followRepository.delete(follow);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getFollowers(Long userId, Long currentUserId, Pageable pageable) {
        return followRepository.findFollowersByUserId(userId, pageable)
                .map(user -> toFollowDTO(user, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FollowDTO> getFollowing(Long userId, Long currentUserId, Pageable pageable) {
        return followRepository.findFollowingByUserId(userId, pageable)
                .map(user -> toFollowDTO(user, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public FollowStatsDTO getFollowStats(Long userId) {
        int followersCount = followRepository.countByFollowingId(userId);
        int followingCount = followRepository.countByFollowerId(userId);
        
        return FollowStatsDTO.builder()
                .userId(userId)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .build();
    }
    
    private FollowDTO toFollowDTO(User user, Long currentUserId) {
        DeveloperProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        boolean isFollowing = currentUserId != null && 
                followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());
        
        return FollowDTO.builder()
                .id(user.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profilePhotoUrl(profile != null ? profile.getProfilePhotoUrl() : null)
                .headline(profile != null ? profile.getHeadline() : null)
                .isFollowing(isFollowing)
                .build();
    }
}