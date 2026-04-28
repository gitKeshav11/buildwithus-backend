package com.buildwithus.profile.service;

import com.buildwithus.profile.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    DeveloperProfileDTO getMyProfile(Long userId);
    DeveloperProfileDTO getProfileByUsername(String username, Long currentUserId);
    DeveloperProfileDTO getProfileById(Long profileId, Long currentUserId);
    DeveloperProfileDTO createProfile(Long userId, CreateProfileRequest request);
    DeveloperProfileDTO updateProfile(Long userId, UpdateProfileRequest request);
    Page<DeveloperProfileDTO> getAllProfiles(Pageable pageable, Long currentUserId);
    Page<DeveloperProfileDTO> searchProfiles(String keyword, Pageable pageable, Long currentUserId);
    Page<DeveloperProfileDTO> filterProfiles(ProfileSearchRequest request, Pageable pageable, Long currentUserId);
    Page<DeveloperProfileDTO> getVerifiedDevelopers(Pageable pageable, Long currentUserId);
    DeveloperProfileDTO uploadProfilePhoto(Long userId, MultipartFile file);
    DeveloperProfileDTO uploadCoverPhoto(Long userId, MultipartFile file);
    void deleteProfilePhoto(Long userId);
    void deleteCoverPhoto(Long userId);
    int calculateProfileCompletion(Long userId);
}