package com.buildwithus.follow.service;

import com.buildwithus.follow.dto.FollowDTO;
import com.buildwithus.follow.dto.FollowStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {
    void follow(Long followerId, Long followingId);
    void unfollow(Long followerId, Long followingId);
    boolean isFollowing(Long followerId, Long followingId);
    Page<FollowDTO> getFollowers(Long userId, Long currentUserId, Pageable pageable);
    Page<FollowDTO> getFollowing(Long userId, Long currentUserId, Pageable pageable);
    FollowStatsDTO getFollowStats(Long userId);
}