package com.buildwithus.hackathon.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.hackathon.dto.*;
import com.buildwithus.hackathon.service.HackathonService;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hackathons")
@RequiredArgsConstructor
@Tag(name = "Hackathons", description = "Hackathon and team finder APIs")
public class HackathonController {
    
    private final HackathonService hackathonService;
    
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a hackathon post")
    public ResponseEntity<ApiResponse<HackathonDTO>> createHackathon(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateHackathonRequest request) {
        HackathonDTO result = hackathonService.createHackathon(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hackathon created", result));
    }
    
    @PutMapping("/{hackathonId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a hackathon")
    public ResponseEntity<ApiResponse<HackathonDTO>> updateHackathon(
            @PathVariable Long hackathonId,
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateHackathonRequest request) {
        HackathonDTO result = hackathonService.updateHackathon(hackathonId, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Hackathon updated", result));
    }
    
    @DeleteMapping("/{hackathonId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a hackathon")
    public ResponseEntity<ApiResponse<Void>> deleteHackathon(
            @PathVariable Long hackathonId,
            @CurrentUser UserPrincipal currentUser) {
        hackathonService.deleteHackathon(hackathonId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Hackathon deleted"));
    }
    
    @GetMapping("/{hackathonId}")
    @Operation(summary = "Get hackathon by ID")
    public ResponseEntity<ApiResponse<HackathonDTO>> getHackathonById(@PathVariable Long hackathonId) {
        HackathonDTO result = hackathonService.getHackathonById(hackathonId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @GetMapping
    @Operation(summary = "Get all hackathons")
    public ResponseEntity<ApiResponse<PagedResponse<HackathonDTO>>> getAllHackathons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var hackathons = hackathonService.getAllHackathons(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(hackathons)));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search hackathons")
    public ResponseEntity<ApiResponse<PagedResponse<HackathonDTO>>> searchHackathons(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var hackathons = hackathonService.searchHackathons(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(hackathons)));
    }
    
    // Team Finder endpoints
    @PostMapping("/team-finder")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a team finder post")
    public ResponseEntity<ApiResponse<TeamFinderPostDTO>> createTeamFinderPost(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateTeamFinderPostRequest request) {
        TeamFinderPostDTO result = hackathonService.createTeamFinderPost(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Team finder post created", result));
    }
    
    @GetMapping("/team-finder")
    @Operation(summary = "Get all team finder posts")
    public ResponseEntity<ApiResponse<PagedResponse<TeamFinderPostDTO>>> getAllTeamFinderPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var posts = hackathonService.getAllTeamFinderPosts(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(posts)));
    }
    
    @GetMapping("/team-finder/type/{type}")
    @Operation(summary = "Get team finder posts by type")
    public ResponseEntity<ApiResponse<PagedResponse<TeamFinderPostDTO>>> getTeamFinderPostsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var posts = hackathonService.getTeamFinderPostsByType(type, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(posts)));
    }
    
    @GetMapping("/team-finder/my-posts")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my team finder posts")
    public ResponseEntity<ApiResponse<PagedResponse<TeamFinderPostDTO>>> getMyTeamFinderPosts(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var posts = hackathonService.getMyTeamFinderPosts(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(posts)));
    }
    
    @GetMapping("/{hackathonId}/team-finder")
    @Operation(summary = "Get team finder posts for a hackathon")
    public ResponseEntity<ApiResponse<PagedResponse<TeamFinderPostDTO>>> getTeamFinderPostsForHackathon(
            @PathVariable Long hackathonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var posts = hackathonService.getTeamFinderPostsForHackathon(hackathonId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(posts)));
    }
    
    @PostMapping("/team-finder/{postId}/join")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Request to join a team")
    public ResponseEntity<ApiResponse<TeamJoinRequestDTO>> requestToJoinTeam(
            @PathVariable Long postId,
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) String message) {
        TeamJoinRequestDTO result = hackathonService.requestToJoinTeam(postId, currentUser.getId(), message);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Join request sent", result));
    }
    
    @PostMapping("/team-finder/join-requests/{requestId}/respond")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Respond to a join request")
    public ResponseEntity<ApiResponse<TeamJoinRequestDTO>> respondToJoinRequest(
            @PathVariable Long requestId,
            @CurrentUser UserPrincipal currentUser,
            @RequestParam boolean accept,
            @RequestParam(required = false) String responseMessage) {
        TeamJoinRequestDTO result = hackathonService.respondToJoinRequest(
                requestId, currentUser.getId(), accept, responseMessage);
        return ResponseEntity.ok(ApiResponse.success("Response recorded", result));
    }
    
    @GetMapping("/team-finder/my-join-requests")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my join requests")
    public ResponseEntity<ApiResponse<PagedResponse<TeamJoinRequestDTO>>> getMyJoinRequests(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var requests = hackathonService.getMyJoinRequests(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(requests)));
    }
    
    @GetMapping("/team-finder/requests-for-my-posts")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get join requests for my posts")
    public ResponseEntity<ApiResponse<PagedResponse<TeamJoinRequestDTO>>> getJoinRequestsForMyPosts(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var requests = hackathonService.getJoinRequestsForMyPosts(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(requests)));
    }
}