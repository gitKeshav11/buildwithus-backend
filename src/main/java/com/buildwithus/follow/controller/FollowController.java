package com.buildwithus.follow.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.follow.dto.FollowDTO;
import com.buildwithus.follow.dto.FollowStatsDTO;
import com.buildwithus.follow.service.FollowService;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
@Tag(name = "Follow System", description = "Follow/Unfollow APIs")
@SecurityRequirement(name = "bearerAuth")
public class FollowController {
    
    private final FollowService followService;
    
    @PostMapping("/{userId}")
    @Operation(summary = "Follow a user")
    public ResponseEntity<ApiResponse<Void>> follow(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long userId) {
        followService.follow(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("Successfully followed user"));
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Unfollow a user")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long userId) {
        followService.unfollow(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("Successfully unfollowed user"));
    }
    
    @GetMapping("/{userId}/followers")
    @Operation(summary = "Get user's followers")
    public ResponseEntity<ApiResponse<PagedResponse<FollowDTO>>> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var followers = followService.getFollowers(userId, currentUserId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(followers)));
    }
    
    @GetMapping("/{userId}/following")
    @Operation(summary = "Get users that a user is following")
    public ResponseEntity<ApiResponse<PagedResponse<FollowDTO>>> getFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var following = followService.getFollowing(userId, currentUserId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(following)));
    }
    
    @GetMapping("/{userId}/stats")
    @Operation(summary = "Get follow stats for a user")
    public ResponseEntity<ApiResponse<FollowStatsDTO>> getFollowStats(@PathVariable Long userId) {
        FollowStatsDTO stats = followService.getFollowStats(userId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @GetMapping("/{userId}/check")
    @Operation(summary = "Check if current user is following a user")
    public ResponseEntity<ApiResponse<Boolean>> checkFollowing(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long userId) {
        boolean isFollowing = followService.isFollowing(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success(isFollowing));
    }
}