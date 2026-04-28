package com.buildwithus.leaderboard.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.leaderboard.dto.LeaderboardEntryDTO;
import com.buildwithus.leaderboard.dto.UserBadgeDTO;
import com.buildwithus.leaderboard.service.LeaderboardService;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Developer leaderboard and badges APIs")
public class LeaderboardController {
    
    private final LeaderboardService leaderboardService;
    
    @GetMapping
    @Operation(summary = "Get top developers leaderboard")
    public ResponseEntity<ApiResponse<PagedResponse<LeaderboardEntryDTO>>> getTopDevelopers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        var leaderboard = leaderboardService.getTopDevelopers(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(leaderboard)));
    }
    
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my leaderboard entry")
    public ResponseEntity<ApiResponse<LeaderboardEntryDTO>> getMyLeaderboardEntry(
            @CurrentUser UserPrincipal currentUser) {
        LeaderboardEntryDTO entry = leaderboardService.getUserLeaderboardEntry(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(entry));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's leaderboard entry")
    public ResponseEntity<ApiResponse<LeaderboardEntryDTO>> getUserLeaderboardEntry(@PathVariable Long userId) {
        LeaderboardEntryDTO entry = leaderboardService.getUserLeaderboardEntry(userId);
        return ResponseEntity.ok(ApiResponse.success(entry));
    }
    
    @GetMapping("/user/{userId}/badges")
    @Operation(summary = "Get user's badges")
    public ResponseEntity<ApiResponse<List<UserBadgeDTO>>> getUserBadges(@PathVariable Long userId) {
        List<UserBadgeDTO> badges = leaderboardService.getUserBadges(userId);
        return ResponseEntity.ok(ApiResponse.success(badges));
    }
    
    @GetMapping("/me/badges")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my badges")
    public ResponseEntity<ApiResponse<List<UserBadgeDTO>>> getMyBadges(
            @CurrentUser UserPrincipal currentUser) {
        List<UserBadgeDTO> badges = leaderboardService.getUserBadges(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(badges));
    }
}