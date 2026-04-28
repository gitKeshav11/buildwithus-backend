package com.buildwithus.verification.service.impl;

import com.buildwithus.common.enums.VerificationStatus;
import com.buildwithus.exception.BadRequestException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.notification.service.NotificationService;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
import com.buildwithus.project.repository.ProjectRepository;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import com.buildwithus.verification.dto.VerificationRequestDTO;
import com.buildwithus.verification.dto.VerificationReviewRequest;
import com.buildwithus.verification.entity.VerificationRequest;
import com.buildwithus.verification.repository.VerificationRequestRepository;
import com.buildwithus.verification.service.VerificationService;
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
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRequestRepository verificationRepository;
    private final UserRepository userRepository;
    private final DeveloperProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    @Override
    public VerificationRequestDTO requestVerification(Long userId) {
        if (verificationRepository.existsByUserIdAndStatus(userId, VerificationStatus.PENDING)) {
            throw new BadRequestException("You already have a pending verification request");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        DeveloperProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        boolean githubLinked = profile.getGithubUrl() != null && !profile.getGithubUrl().isBlank();
        boolean linkedinLinked = profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isBlank();
        boolean portfolioSubmitted = profile.getPortfolioUrl() != null && !profile.getPortfolioUrl().isBlank();
        boolean profileComplete = profile.getProfileCompletionPercentage() >= 70;
        boolean hasProjects = projectRepository.findByOwnerId(userId, Pageable.unpaged()).getTotalElements() > 0;

        VerificationRequest request = VerificationRequest.builder()
                .user(user)
                .status(VerificationStatus.PENDING)
                .githubLinked(githubLinked)
                .linkedinLinked(linkedinLinked)
                .portfolioSubmitted(portfolioSubmitted)
                .profileComplete(profileComplete)
                .hasProjects(hasProjects)
                .build();

        final VerificationRequest savedRequest = verificationRepository.save(request); //  FIX
        return toDTO(savedRequest); // use final variable
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationRequestDTO getMyVerificationStatus(Long userId) {
        VerificationRequest request = verificationRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);

        if (request == null) {
            return VerificationRequestDTO.builder()
                    .userId(userId)
                    .status(null)
                    .build();
        }

        return toDTO(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VerificationRequestDTO> getPendingVerificationRequests(Pageable pageable) {
        return verificationRepository.findByStatus(VerificationStatus.PENDING, pageable)
                .map(request -> toDTO(request)); //  FIX (explicit lambda)
    }

    @Override
    public VerificationRequestDTO reviewVerificationRequest(Long requestId, Long adminId, VerificationReviewRequest reviewRequest) {
        VerificationRequest request = verificationRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("VerificationRequest", "id", requestId));

        if (request.getStatus() != VerificationStatus.PENDING) {
            throw new BadRequestException("This request has already been processed");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminId));

        request.setReviewedBy(admin);
        request.setAdminNotes(reviewRequest.getAdminNotes());

        final Boolean isApproved = reviewRequest.getApprove(); //  already correct

        if (Boolean.TRUE.equals(isApproved)) {
            request.setStatus(VerificationStatus.APPROVED);

            DeveloperProfile profile = profileRepository.findByUserId(request.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", request.getUser().getId()));
            profile.setIsVerified(true);
            profileRepository.save(profile);

            notificationService.sendVerificationApprovedNotification(request.getUser().getId());
        } else {
            request.setStatus(VerificationStatus.REJECTED);
            notificationService.sendVerificationRejectedNotification(request.getUser().getId());
        }

        final VerificationRequest savedRequest = verificationRepository.save(request); // FIX
        return toDTO(savedRequest); //  use final variable
    }

    private VerificationRequestDTO toDTO(VerificationRequest request) {
        return VerificationRequestDTO.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .username(request.getUser().getUsername())
                .fullName(request.getUser().getFullName())
                .email(request.getUser().getEmail())
                .status(request.getStatus())
                .githubLinked(request.getGithubLinked())
                .linkedinLinked(request.getLinkedinLinked())
                .portfolioSubmitted(request.getPortfolioSubmitted())
                .profileComplete(request.getProfileComplete())
                .hasProjects(request.getHasProjects())
                .adminNotes(request.getAdminNotes())
                .createdAt(request.getCreatedAt())
                .build();
    }
}