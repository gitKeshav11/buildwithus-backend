package com.buildwithus.hackathon.service.impl;

import com.buildwithus.common.enums.RequestStatus;
import com.buildwithus.common.enums.TeamFinderType;
import com.buildwithus.exception.BadRequestException;
import com.buildwithus.exception.ForbiddenException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.hackathon.dto.*;
import com.buildwithus.hackathon.entity.Hackathon;
import com.buildwithus.hackathon.entity.TeamFinderPost;
import com.buildwithus.hackathon.entity.TeamJoinRequest;
import com.buildwithus.hackathon.repository.HackathonRepository;
import com.buildwithus.hackathon.repository.TeamFinderPostRepository;
import com.buildwithus.hackathon.repository.TeamJoinRequestRepository;
import com.buildwithus.hackathon.service.HackathonService;
import com.buildwithus.notification.service.NotificationService;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
import com.buildwithus.user.entity.Role;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HackathonServiceImpl implements HackathonService {
    
    private final HackathonRepository hackathonRepository;
    private final TeamFinderPostRepository teamFinderPostRepository;
    private final TeamJoinRequestRepository joinRequestRepository;
    private final UserRepository userRepository;
    private final DeveloperProfileRepository profileRepository;
    private final NotificationService notificationService;
    
    @Override
    public HackathonDTO createHackathon(Long userId, CreateHackathonRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Hackathon hackathon = Hackathon.builder()
                .title(request.getTitle())
                .organizer(request.getOrganizer())
                .description(request.getDescription())
                .registrationLink(request.getRegistrationLink())
                .eventDate(request.getEventDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .isOnline(request.getIsOnline() != null ? request.getIsOnline() : false)
                .teamSize(request.getTeamSize())
                .prizeInfo(request.getPrizeInfo())
                .isActive(true)
                .postedBy(user)
                .requiredRoles(request.getRequiredRoles() != null ? request.getRequiredRoles() : new HashSet<>())
                .tags(request.getTags() != null ? request.getTags() : new HashSet<>())
                .build();
        
        hackathon = hackathonRepository.save(hackathon);
        return toHackathonDTO(hackathon);
    }
    
    @Override
    public HackathonDTO updateHackathon(Long hackathonId, CreateHackathonRequest request, Long userId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon", "id", hackathonId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (!user.hasRole(Role.ADMIN) && !hackathon.getPostedBy().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this hackathon");
        }
        
        if (request.getTitle() != null) hackathon.setTitle(request.getTitle());
        if (request.getOrganizer() != null) hackathon.setOrganizer(request.getOrganizer());
        if (request.getDescription() != null) hackathon.setDescription(request.getDescription());
        if (request.getRegistrationLink() != null) hackathon.setRegistrationLink(request.getRegistrationLink());
        if (request.getEventDate() != null) hackathon.setEventDate(request.getEventDate());
        if (request.getEndDate() != null) hackathon.setEndDate(request.getEndDate());
        if (request.getLocation() != null) hackathon.setLocation(request.getLocation());
        if (request.getIsOnline() != null) hackathon.setIsOnline(request.getIsOnline());
        if (request.getTeamSize() != null) hackathon.setTeamSize(request.getTeamSize());
        if (request.getPrizeInfo() != null) hackathon.setPrizeInfo(request.getPrizeInfo());
        if (request.getRequiredRoles() != null) hackathon.setRequiredRoles(request.getRequiredRoles());
        if (request.getTags() != null) hackathon.setTags(request.getTags());
        
        hackathon = hackathonRepository.save(hackathon);
        return toHackathonDTO(hackathon);
    }
    
    @Override
    public void deleteHackathon(Long hackathonId, Long userId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon", "id", hackathonId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (!user.hasRole(Role.ADMIN) && !hackathon.getPostedBy().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this hackathon");
        }
        
        hackathon.setIsDeleted(true);
        hackathon.setIsActive(false);
        hackathonRepository.save(hackathon);
    }
    
    @Override
    @Transactional(readOnly = true)
    public HackathonDTO getHackathonById(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon", "id", hackathonId));
        return toHackathonDTO(hackathon);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<HackathonDTO> getAllHackathons(Pageable pageable) {
        return hackathonRepository.findAllActive(pageable).map(this::toHackathonDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<HackathonDTO> searchHackathons(String keyword, Pageable pageable) {
        return hackathonRepository.searchHackathons(keyword, pageable).map(this::toHackathonDTO);
    }
    
    @Override
    public TeamFinderPostDTO createTeamFinderPost(Long userId, CreateTeamFinderPostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Hackathon hackathon = null;
        if (request.getHackathonId() != null) {
            hackathon = hackathonRepository.findById(request.getHackathonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hackathon", "id", request.getHackathonId()));
        }
        
        TeamFinderPost post = TeamFinderPost.builder()
                .user(user)
                .hackathon(hackathon)
                .postType(request.getPostType())
                .title(request.getTitle())
                .message(request.getMessage())
                .rolesNeeded(request.getRolesNeeded() != null ? request.getRolesNeeded() : new HashSet<>())
                .skillsRequired(request.getSkillsRequired() != null ? request.getSkillsRequired() : new HashSet<>())
                .preferredExperience(request.getPreferredExperience())
                .isActive(true)
                .build();
        
        post = teamFinderPostRepository.save(post);
        return toTeamFinderPostDTO(post);
    }
    
    @Override
    public TeamFinderPostDTO updateTeamFinderPost(Long postId, CreateTeamFinderPostRequest request, Long userId) {
        TeamFinderPost post = teamFinderPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamFinderPost", "id", postId));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this post");
        }
        
        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getMessage() != null) post.setMessage(request.getMessage());
        if (request.getRolesNeeded() != null) post.setRolesNeeded(request.getRolesNeeded());
        if (request.getSkillsRequired() != null) post.setSkillsRequired(request.getSkillsRequired());
        if (request.getPreferredExperience() != null) post.setPreferredExperience(request.getPreferredExperience());
        
        post = teamFinderPostRepository.save(post);
        return toTeamFinderPostDTO(post);
    }
    
    @Override
    public void deleteTeamFinderPost(Long postId, Long userId) {
        TeamFinderPost post = teamFinderPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamFinderPost", "id", postId));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this post");
        }
        
        post.setIsDeleted(true);
        post.setIsActive(false);
        teamFinderPostRepository.save(post);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TeamFinderPostDTO> getAllTeamFinderPosts(Pageable pageable) {
        return teamFinderPostRepository.findAllActive(pageable).map(this::toTeamFinderPostDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TeamFinderPostDTO> getTeamFinderPostsByType(String type, Pageable pageable) {
        TeamFinderType postType = TeamFinderType.valueOf(type.toUpperCase());
        return teamFinderPostRepository.findByType(postType, pageable).map(this::toTeamFinderPostDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TeamFinderPostDTO> getMyTeamFinderPosts(Long userId, Pageable pageable) {
        return teamFinderPostRepository.findByUserId(userId, pageable).map(this::toTeamFinderPostDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TeamFinderPostDTO> getTeamFinderPostsForHackathon(Long hackathonId, Pageable pageable) {
        return teamFinderPostRepository.findByHackathonId(hackathonId, pageable).map(this::toTeamFinderPostDTO);
    }
    
    @Override
    public TeamJoinRequestDTO requestToJoinTeam(Long postId, Long userId, String message) {
        TeamFinderPost post = teamFinderPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamFinderPost", "id", postId));
        
        if (post.getUser().getId().equals(userId)) {
            throw new BadRequestException("You cannot request to join your own team");
        }
        
        if (joinRequestRepository.existsByPostIdAndRequesterId(postId, userId)) {
            throw new BadRequestException("You have already requested to join this team");
        }
        
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        TeamJoinRequest request = TeamJoinRequest.builder()
                .post(post)
                .requester(requester)
                .message(message)
                .status(RequestStatus.PENDING)
                .build();
        
        request = joinRequestRepository.save(request);
        
        // Send notification
        notificationService.sendTeamJoinRequestNotification(post.getUser().getId(), userId, postId);
        
        return toJoinRequestDTO(request);
    }
    
    @Override
    public TeamJoinRequestDTO respondToJoinRequest(Long requestId, Long userId, boolean accept, String responseMessage) {
        TeamJoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamJoinRequest", "id", requestId));
        
        if (!request.getPost().getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to respond to this request");
        }
        
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("This request has already been processed");
        }
        
        request.setStatus(accept ? RequestStatus.ACCEPTED : RequestStatus.REJECTED);
        request.setResponseMessage(responseMessage);
        request = joinRequestRepository.save(request);
        
        // Send notification
        if (accept) {
            notificationService.sendTeamJoinAcceptedNotification(request.getRequester().getId(), request.getPost().getId());
        } else {
            notificationService.sendTeamJoinRejectedNotification(request.getRequester().getId(), request.getPost().getId());
        }
        
        return toJoinRequestDTO(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TeamJoinRequestDTO> getMyJoinRequests(Long userId, Pageable pageable) {
        return joinRequestRepository.findByRequesterId(userId, pageable).map(this::toJoinRequestDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TeamJoinRequestDTO> getJoinRequestsForMyPosts(Long userId, Pageable pageable) {
        return joinRequestRepository.findByPostOwnerIdAndStatus(userId, RequestStatus.PENDING, pageable)
                .map(this::toJoinRequestDTO);
    }
    
    private HackathonDTO toHackathonDTO(Hackathon hackathon) {
        return HackathonDTO.builder()
                .id(hackathon.getId())
                .title(hackathon.getTitle())
                .organizer(hackathon.getOrganizer())
                .description(hackathon.getDescription())
                .registrationLink(hackathon.getRegistrationLink())
                .eventDate(hackathon.getEventDate())
                .endDate(hackathon.getEndDate())
                .location(hackathon.getLocation())
                .isOnline(hackathon.getIsOnline())
                .teamSize(hackathon.getTeamSize())
                .prizeInfo(hackathon.getPrizeInfo())
                .isActive(hackathon.getIsActive())
                .postedById(hackathon.getPostedBy() != null ? hackathon.getPostedBy().getId() : null)
                .postedByName(hackathon.getPostedBy() != null ? hackathon.getPostedBy().getFullName() : null)
                .requiredRoles(hackathon.getRequiredRoles())
                .tags(hackathon.getTags())
                .createdAt(hackathon.getCreatedAt())
                .build();
    }
    
    private TeamFinderPostDTO toTeamFinderPostDTO(TeamFinderPost post) {
        DeveloperProfile profile = profileRepository.findByUserId(post.getUser().getId()).orElse(null);
        
        return TeamFinderPostDTO.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .fullName(post.getUser().getFullName())
                .profilePhotoUrl(profile != null ? profile.getProfilePhotoUrl() : null)
                .hackathonId(post.getHackathon() != null ? post.getHackathon().getId() : null)
                .hackathonTitle(post.getHackathon() != null ? post.getHackathon().getTitle() : null)
                .postType(post.getPostType())
                .title(post.getTitle())
                .message(post.getMessage())
                .rolesNeeded(post.getRolesNeeded())
                .skillsRequired(post.getSkillsRequired())
                .preferredExperience(post.getPreferredExperience())
                .isActive(post.getIsActive())
                .createdAt(post.getCreatedAt())
                .build();
    }
    
    private TeamJoinRequestDTO toJoinRequestDTO(TeamJoinRequest request) {
        DeveloperProfile profile = profileRepository.findByUserId(request.getRequester().getId()).orElse(null);
        
        return TeamJoinRequestDTO.builder()
                .id(request.getId())
                .postId(request.getPost().getId())
                .postTitle(request.getPost().getTitle())
                .requesterId(request.getRequester().getId())
                .requesterName(request.getRequester().getFullName())
                .requesterUsername(request.getRequester().getUsername())
                .requesterProfilePhotoUrl(profile != null ? profile.getProfilePhotoUrl() : null)
                .message(request.getMessage())
                .status(request.getStatus())
                .responseMessage(request.getResponseMessage())
                .createdAt(request.getCreatedAt())
                .build();
    }
}