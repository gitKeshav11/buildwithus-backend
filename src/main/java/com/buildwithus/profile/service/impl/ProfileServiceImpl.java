package com.buildwithus.profile.service.impl;

import com.buildwithus.exception.BadRequestException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.follow.repository.FollowRepository;
import com.buildwithus.profile.dto.*;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.entity.Skill;
import com.buildwithus.profile.entity.TechStack;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
import com.buildwithus.profile.repository.SkillRepository;
import com.buildwithus.profile.repository.TechStackRepository;
import com.buildwithus.profile.service.ProfileService;
import com.buildwithus.upload.service.CloudinaryService;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final DeveloperProfileRepository profileRepository;
    private final SkillRepository skillRepository;
    private final TechStackRepository techStackRepository;
    private final FollowRepository followRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DeveloperProfileDTO getMyProfile(Long userId) {
        DeveloperProfile profile = getOrCreateProfile(userId);
        return toDTO(profile, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public DeveloperProfileDTO getProfileByUsername(String username, Long currentUserId) {
        DeveloperProfile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "username", username));

        if (!Boolean.TRUE.equals(profile.getProfileVisibility())
                && !profile.getUser().getId().equals(currentUserId)) {
            throw new ResourceNotFoundException("Profile", "username", username);
        }

        return toDTO(profile, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public DeveloperProfileDTO getProfileById(Long profileId, Long currentUserId) {
        DeveloperProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileId));

        if (!Boolean.TRUE.equals(profile.getProfileVisibility())
                && !profile.getUser().getId().equals(currentUserId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return toDTO(profile, currentUserId);
    }

    @Override
    public DeveloperProfileDTO createProfile(Long userId, CreateProfileRequest request) {
        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException("Profile already exists for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String username = user.getUsername();
        if (username == null || username.isBlank()) {
            username = generateUsernameFromEmail(user.getEmail());
            user.setUsername(username);
            userRepository.save(user);
        }

        DeveloperProfile profile = DeveloperProfile.builder()
                .user(user)
                .fullName(request.getFullName() != null ? request.getFullName() : user.getFullName())
                .username(username)
                .email(user.getEmail())
                .headline(request.getHeadline())
                .bio(request.getBio())
                .location(request.getLocation())
                .collegeOrCompany(request.getCollegeOrCompany())
                .experienceLevel(request.getExperienceLevel())
                .availabilityStatus(request.getAvailabilityStatus())
                .primaryRole(request.getPrimaryRole())
                .yearsOfExperience(request.getYearsOfExperience())
                .githubUrl(request.getGithubUrl())
                .linkedinUrl(request.getLinkedinUrl())
                .portfolioUrl(request.getPortfolioUrl())
                .twitterUrl(request.getTwitterUrl())
                .leetcodeUrl(request.getLeetcodeUrl())
                .codeforcesUrl(request.getCodeforcesUrl())
                .resumeUrl(request.getResumeUrl())
                .profileVisibility(request.getProfileVisibility() != null ? request.getProfileVisibility() : true)
                .isVerified(user.getEmailVerified() != null ? user.getEmailVerified() : false)
                .profileCompletionPercentage(10)
                .build();

        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            profile.setSkills(processSkills(request.getSkills()));
        }

        if (request.getTechStacks() != null && !request.getTechStacks().isEmpty()) {
            profile.setTechStacks(processTechStacks(request.getTechStacks()));
        }

        profile.setProfileCompletionPercentage(calculateProfileCompletionInternal(profile));
        profile = profileRepository.save(profile);

        return toDTO(profile, userId);
    }

    @Override
    public DeveloperProfileDTO updateProfile(Long userId, UpdateProfileRequest request) {
        DeveloperProfile profile = getOrCreateProfile(userId);

        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getCollegeOrCompany() != null) profile.setCollegeOrCompany(request.getCollegeOrCompany());
        if (request.getExperienceLevel() != null) profile.setExperienceLevel(request.getExperienceLevel());
        if (request.getAvailabilityStatus() != null) profile.setAvailabilityStatus(request.getAvailabilityStatus());
        if (request.getPrimaryRole() != null) profile.setPrimaryRole(request.getPrimaryRole());
        if (request.getYearsOfExperience() != null) profile.setYearsOfExperience(request.getYearsOfExperience());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getTwitterUrl() != null) profile.setTwitterUrl(request.getTwitterUrl());
        if (request.getLeetcodeUrl() != null) profile.setLeetcodeUrl(request.getLeetcodeUrl());
        if (request.getCodeforcesUrl() != null) profile.setCodeforcesUrl(request.getCodeforcesUrl());
        if (request.getResumeUrl() != null) profile.setResumeUrl(request.getResumeUrl());
        if (request.getProfileVisibility() != null) profile.setProfileVisibility(request.getProfileVisibility());

        if (request.getSkills() != null) {
            profile.setSkills(processSkills(request.getSkills()));
        }

        if (request.getTechStacks() != null) {
            profile.setTechStacks(processTechStacks(request.getTechStacks()));
        }

        profile.setProfileCompletionPercentage(calculateProfileCompletionInternal(profile));
        profile = profileRepository.save(profile);

        return toDTO(profile, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeveloperProfileDTO> getAllProfiles(Pageable pageable, Long currentUserId) {
        Page<DeveloperProfile> profiles = profileRepository.findAllPublic(pageable);

        return new PageImpl<>(
                profiles.getContent().stream()
                        .map(profile -> toDTO(profile, currentUserId))
                        .collect(Collectors.toList()),
                pageable,
                profiles.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeveloperProfileDTO> searchProfiles(String keyword, Pageable pageable, Long currentUserId) {
        return profileRepository.searchProfiles(keyword, pageable)
                .map(profile -> toDTO(profile, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeveloperProfileDTO> filterProfiles(ProfileSearchRequest request, Pageable pageable, Long currentUserId) {
        return profileRepository.findByFilters(
                request.getRole(),
                request.getExperienceLevel(),
                request.getAvailabilityStatus(),
                request.getLocation(),
                pageable
        ).map(profile -> toDTO(profile, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeveloperProfileDTO> getVerifiedDevelopers(Pageable pageable, Long currentUserId) {
        return profileRepository.findVerifiedDevelopers(pageable)
                .map(profile -> toDTO(profile, currentUserId));
    }

    @Override
    public DeveloperProfileDTO uploadProfilePhoto(Long userId, MultipartFile file) {
        DeveloperProfile profile = getOrCreateProfile(userId);

        String imageUrl = cloudinaryService.uploadImage(file, "profile-photos");
        profile.setProfilePhotoUrl(imageUrl);
        profile.setProfileCompletionPercentage(calculateProfileCompletionInternal(profile));
        profile = profileRepository.save(profile);

        return toDTO(profile, userId);
    }

    @Override
    public DeveloperProfileDTO uploadCoverPhoto(Long userId, MultipartFile file) {
        DeveloperProfile profile = getOrCreateProfile(userId);

        String imageUrl = cloudinaryService.uploadImage(file, "cover-photos");
        profile.setCoverPhotoUrl(imageUrl);
        profile = profileRepository.save(profile);

        return toDTO(profile, userId);
    }

    @Override
    public void deleteProfilePhoto(Long userId) {
        DeveloperProfile profile = getOrCreateProfile(userId);

        if (profile.getProfilePhotoUrl() != null) {
            cloudinaryService.deleteImage(profile.getProfilePhotoUrl());
            profile.setProfilePhotoUrl(null);
            profile.setProfileCompletionPercentage(calculateProfileCompletionInternal(profile));
            profileRepository.save(profile);
        }
    }

    @Override
    public void deleteCoverPhoto(Long userId) {
        DeveloperProfile profile = getOrCreateProfile(userId);

        if (profile.getCoverPhotoUrl() != null) {
            cloudinaryService.deleteImage(profile.getCoverPhotoUrl());
            profile.setCoverPhotoUrl(null);
            profileRepository.save(profile);
        }
    }

    @Override
    public int calculateProfileCompletion(Long userId) {
        DeveloperProfile profile = getOrCreateProfile(userId);
        return calculateProfileCompletionInternal(profile);
    }

    private DeveloperProfile getOrCreateProfile(Long userId) {
        return profileRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            String username = user.getUsername();

            if (username == null || username.isBlank()) {
                username = generateUsernameFromEmail(user.getEmail());
                user.setUsername(username);
                userRepository.save(user);
            }

            String fullName = user.getFullName();
            if (fullName == null || fullName.isBlank()) {
                fullName = username;
            }

            DeveloperProfile profile = DeveloperProfile.builder()
                    .user(user)
                    .fullName(fullName)
                    .username(username)
                    .email(user.getEmail())
                    .profileVisibility(true)
                    .isVerified(user.getEmailVerified() != null ? user.getEmailVerified() : false)
                    .profileCompletionPercentage(10)
                    .build();

            return profileRepository.save(profile);
        });
    }

    private String generateUsernameFromEmail(String email) {
        if (email == null || email.isBlank()) {
            return "user" + System.currentTimeMillis();
        }

        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9_-]", "");
        if (base.isBlank()) {
            base = "user";
        }

        String username = base;
        int counter = 1;

        while (profileRepository.findByUsername(username).isPresent()) {
            username = base + counter++;
        }

        return username;
    }

    private int calculateProfileCompletionInternal(DeveloperProfile profile) {
        int completion = 0;
        int totalFields = 15;

        if (profile.getFullName() != null && !profile.getFullName().isBlank()) completion++;
        if (profile.getHeadline() != null && !profile.getHeadline().isBlank()) completion++;
        if (profile.getBio() != null && !profile.getBio().isBlank()) completion++;
        if (profile.getLocation() != null && !profile.getLocation().isBlank()) completion++;
        if (profile.getProfilePhotoUrl() != null) completion++;
        if (profile.getPrimaryRole() != null) completion++;
        if (profile.getExperienceLevel() != null) completion++;
        if (profile.getAvailabilityStatus() != null) completion++;
        if (profile.getGithubUrl() != null && !profile.getGithubUrl().isBlank()) completion++;
        if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isBlank()) completion++;
        if (profile.getPortfolioUrl() != null && !profile.getPortfolioUrl().isBlank()) completion++;
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) completion++;
        if (profile.getTechStacks() != null && !profile.getTechStacks().isEmpty()) completion++;
        if (profile.getCollegeOrCompany() != null && !profile.getCollegeOrCompany().isBlank()) completion++;
        if (profile.getResumeUrl() != null && !profile.getResumeUrl().isBlank()) completion++;

        return (completion * 100) / totalFields;
    }

    private Set<Skill> processSkills(Set<String> skillNames) {
        Set<Skill> skills = new HashSet<>();

        for (String skillName : skillNames) {
            if (skillName == null || skillName.isBlank()) continue;

            String cleanName = skillName.trim();

            Skill skill = skillRepository.findByNameIgnoreCase(cleanName)
                    .orElseGet(() -> skillRepository.save(Skill.builder().name(cleanName).build()));

            skills.add(skill);
        }

        return skills;
    }

    private Set<TechStack> processTechStacks(Set<String> techStackNames) {
        Set<TechStack> techStacks = new HashSet<>();

        for (String techStackName : techStackNames) {
            if (techStackName == null || techStackName.isBlank()) continue;

            String cleanName = techStackName.trim();

            TechStack techStack = techStackRepository.findByNameIgnoreCase(cleanName)
                    .orElseGet(() -> techStackRepository.save(TechStack.builder().name(cleanName).build()));

            techStacks.add(techStack);
        }

        return techStacks;
    }

    private DeveloperProfileDTO toDTO(DeveloperProfile profile, Long currentUserId) {
        int followersCount = followRepository.countByFollowingId(profile.getUser().getId());
        int followingCount = followRepository.countByFollowerId(profile.getUser().getId());

        boolean isFollowing = currentUserId != null &&
                followRepository.existsByFollowerIdAndFollowingId(currentUserId, profile.getUser().getId());

        return DeveloperProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getFullName())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .headline(profile.getHeadline())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .collegeOrCompany(profile.getCollegeOrCompany())
                .experienceLevel(profile.getExperienceLevel())
                .availabilityStatus(profile.getAvailabilityStatus())
                .primaryRole(profile.getPrimaryRole())
                .yearsOfExperience(profile.getYearsOfExperience())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .coverPhotoUrl(profile.getCoverPhotoUrl())
                .githubUrl(profile.getGithubUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .portfolioUrl(profile.getPortfolioUrl())
                .twitterUrl(profile.getTwitterUrl())
                .leetcodeUrl(profile.getLeetcodeUrl())
                .codeforcesUrl(profile.getCodeforcesUrl())
                .resumeUrl(profile.getResumeUrl())
                .profileVisibility(profile.getProfileVisibility())
                .profileCompletionPercentage(profile.getProfileCompletionPercentage())
                .isVerified(profile.getIsVerified())
                .skills(profile.getSkills() != null
                        ? profile.getSkills().stream()
                        .map(s -> SkillDTO.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .category(s.getCategory())
                                .build())
                        .collect(Collectors.toSet())
                        : new HashSet<>())
                .techStacks(profile.getTechStacks() != null
                        ? profile.getTechStacks().stream()
                        .map(t -> TechStackDTO.builder()
                                .id(t.getId())
                                .name(t.getName())
                                .category(t.getCategory())
                                .iconUrl(t.getIconUrl())
                                .build())
                        .collect(Collectors.toSet())
                        : new HashSet<>())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .createdAt(profile.getCreatedAt())
                .build();
    }
}