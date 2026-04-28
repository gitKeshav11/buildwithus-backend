package com.buildwithus.profile.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.common.enums.AvailabilityStatus;
import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.profile.dto.*;
import com.buildwithus.profile.service.ProfileService;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Developer Profiles", description = "Developer profile management APIs")
public class ProfileController {
    
    private final ProfileService profileService;
    
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<DeveloperProfileDTO>> getMyProfile(@CurrentUser UserPrincipal currentUser) {
        DeveloperProfileDTO profile = profileService.getMyProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update current user's profile")
    public ResponseEntity<ApiResponse<DeveloperProfileDTO>> updateMyProfile(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        DeveloperProfileDTO profile = profileService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }
    
    @GetMapping("/{username}")
    @Operation(summary = "Get profile by username")
    public ResponseEntity<ApiResponse<DeveloperProfileDTO>> getProfileByUsername(
            @PathVariable String username,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        DeveloperProfileDTO profile = profileService.getProfileByUsername(username, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @GetMapping
    @Operation(summary = "Get all profiles")
    public ResponseEntity<ApiResponse<PagedResponse<DeveloperProfileDTO>>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var profiles = profileService.getAllProfiles(PageRequest.of(page, size, sort), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(profiles)));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search profiles")
    public ResponseEntity<ApiResponse<PagedResponse<DeveloperProfileDTO>>> searchProfiles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var profiles = profileService.searchProfiles(keyword, PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(profiles)));
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filter profiles")
    public ResponseEntity<ApiResponse<PagedResponse<DeveloperProfileDTO>>> filterProfiles(
            @RequestParam(required = false) PrimaryRole role,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) AvailabilityStatus availabilityStatus,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        ProfileSearchRequest request = ProfileSearchRequest.builder()
                .role(role)
                .experienceLevel(experienceLevel)
                .availabilityStatus(availabilityStatus)
                .location(location)
                .build();
        var profiles = profileService.filterProfiles(request, PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(profiles)));
    }
    
    @GetMapping("/verified")
    @Operation(summary = "Get verified developers")
    public ResponseEntity<ApiResponse<PagedResponse<DeveloperProfileDTO>>> getVerifiedDevelopers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var profiles = profileService.getVerifiedDevelopers(PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(profiles)));
    }
    
    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload profile photo")
    public ResponseEntity<ApiResponse<DeveloperProfileDTO>> uploadProfilePhoto(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam("file") MultipartFile file) {
        DeveloperProfileDTO profile = profileService.uploadProfilePhoto(currentUser.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Profile photo uploaded successfully", profile));
    }
    
    @PostMapping(value = "/me/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload cover photo")
    public ResponseEntity<ApiResponse<DeveloperProfileDTO>> uploadCoverPhoto(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam("file") MultipartFile file) {
        DeveloperProfileDTO profile = profileService.uploadCoverPhoto(currentUser.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Cover photo uploaded successfully", profile));
    }
    
    @DeleteMapping("/me/photo")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete profile photo")
    public ResponseEntity<ApiResponse<Void>> deleteProfilePhoto(@CurrentUser UserPrincipal currentUser) {
        profileService.deleteProfilePhoto(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile photo deleted successfully"));
    }
    
    @DeleteMapping("/me/cover")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete cover photo")
    public ResponseEntity<ApiResponse<Void>> deleteCoverPhoto(@CurrentUser UserPrincipal currentUser) {
        profileService.deleteCoverPhoto(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Cover photo deleted successfully"));
    }
}