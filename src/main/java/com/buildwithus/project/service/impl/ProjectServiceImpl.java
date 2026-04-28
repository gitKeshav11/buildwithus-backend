package com.buildwithus.project.service.impl;

import com.buildwithus.common.enums.CollaborationStatus;
import com.buildwithus.common.enums.RequestStatus;
import com.buildwithus.exception.BadRequestException;
import com.buildwithus.exception.ForbiddenException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.notification.service.NotificationService;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
import com.buildwithus.project.dto.*;
import com.buildwithus.project.entity.*;
import com.buildwithus.project.repository.*;
import com.buildwithus.project.service.ProjectService;
import com.buildwithus.upload.service.CloudinaryService;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectServiceImpl implements ProjectService {
    
    private final ProjectRepository projectRepository;
    private final ProjectCollaboratorRepository collaboratorRepository;
    private final CollaborationRequestRepository requestRepository;
    private final ProjectImageRepository imageRepository;
    private final UserRepository userRepository;
    private final DeveloperProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    
    @Override
    public ProjectDTO createProject(Long userId, CreateProjectRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        String slug = generateSlug(request.getTitle());
        
        Project project = Project.builder()
                .title(request.getTitle())
                .slug(slug)
                .shortDescription(request.getShortDescription())
                .detailedDescription(request.getDetailedDescription())
                .category(request.getCategory())
                .projectStage(request.getProjectStage())
                .collaborationStatus(request.getCollaborationStatus() != null ? 
                        request.getCollaborationStatus() : CollaborationStatus.CLOSED)
                .collaboratorsNeeded(request.getCollaboratorsNeeded())
                .isVisible(request.getIsVisible() != null ? request.getIsVisible() : true)
                .owner(owner)
                .githubRepoUrl(request.getGithubRepoUrl())
                .liveDemoUrl(request.getLiveDemoUrl())
                .documentationUrl(request.getDocumentationUrl())
                .videoDemoUrl(request.getVideoDemoUrl())
                .websiteUrl(request.getWebsiteUrl())
                .techStack(request.getTechStack() != null ? request.getTechStack() : new HashSet<>())
                .rolesNeeded(request.getRolesNeeded() != null ? request.getRolesNeeded() : new HashSet<>())
                .viewsCount(0)
                .build();
        
        project = projectRepository.save(project);
        
        // Add owner as collaborator
        ProjectCollaborator ownerCollaborator = ProjectCollaborator.builder()
                .project(project)
                .user(owner)
                .isOwner(true)
                .build();
        collaboratorRepository.save(ownerCollaborator);
        
        return toDTO(project, userId);
    }
    
    @Override
    public ProjectDTO updateProject(Long projectId, UpdateProjectRequest request, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this project");
        }
        
        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
            project.setSlug(generateSlug(request.getTitle()));
        }
        if (request.getShortDescription() != null) project.setShortDescription(request.getShortDescription());
        if (request.getDetailedDescription() != null) project.setDetailedDescription(request.getDetailedDescription());
        if (request.getCategory() != null) project.setCategory(request.getCategory());
        if (request.getProjectStage() != null) project.setProjectStage(request.getProjectStage());
        if (request.getCollaborationStatus() != null) project.setCollaborationStatus(request.getCollaborationStatus());
        if (request.getCollaboratorsNeeded() != null) project.setCollaboratorsNeeded(request.getCollaboratorsNeeded());
        if (request.getIsVisible() != null) project.setIsVisible(request.getIsVisible());
        if (request.getGithubRepoUrl() != null) project.setGithubRepoUrl(request.getGithubRepoUrl());
        if (request.getLiveDemoUrl() != null) project.setLiveDemoUrl(request.getLiveDemoUrl());
        if (request.getDocumentationUrl() != null) project.setDocumentationUrl(request.getDocumentationUrl());
        if (request.getVideoDemoUrl() != null) project.setVideoDemoUrl(request.getVideoDemoUrl());
        if (request.getWebsiteUrl() != null) project.setWebsiteUrl(request.getWebsiteUrl());
        if (request.getTechStack() != null) project.setTechStack(request.getTechStack());
        if (request.getRolesNeeded() != null) project.setRolesNeeded(request.getRolesNeeded());
        
        project = projectRepository.save(project);
        return toDTO(project, userId);
    }
    
    @Override
    public void deleteProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this project");
        }
        
        project.setIsDeleted(true);
        project.setIsVisible(false);
        projectRepository.save(project);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long projectId, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        // Increment view count
        project.setViewsCount(project.getViewsCount() + 1);
        projectRepository.save(project);
        
        return toDTO(project, currentUserId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getProjectBySlug(String slug, Long currentUserId) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "slug", slug));
        
        project.setViewsCount(project.getViewsCount() + 1);
        projectRepository.save(project);
        
        return toDTO(project, currentUserId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> getAllProjects(Pageable pageable, Long currentUserId) {
        return projectRepository.findAllVisible(pageable)
                .map(project -> toDTO(project, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> searchProjects(String keyword, Pageable pageable, Long currentUserId) {
        return projectRepository.searchProjects(keyword, pageable)
                .map(project -> toDTO(project, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> filterProjects(ProjectSearchRequest request, Pageable pageable, Long currentUserId) {
        return projectRepository.findByFilters(
                request.getCategory(),
                request.getProjectStage(),
                request.getCollaborationStatus(),
                pageable
        ).map(project -> toDTO(project, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> getMyProjects(Long userId, Pageable pageable) {
        return projectRepository.findByOwnerId(userId, pageable)
                .map(project -> toDTO(project, userId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> getOpenForCollaboration(Pageable pageable, Long currentUserId) {
        return projectRepository.findOpenForCollaboration(pageable)
                .map(project -> toDTO(project, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> getProjectsAsCollaborator(Long userId, Pageable pageable) {
        return collaboratorRepository.findProjectsByCollaboratorId(userId, pageable)
                .map(project -> toDTO(project, userId));
    }
    
    @Override
    public CollaborationRequestDTO requestCollaboration(Long projectId, Long userId, CollaborationRequestCreate request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        if (project.getOwner().getId().equals(userId)) {
            throw new BadRequestException("You cannot request to collaborate on your own project");
        }
        
        if (project.getCollaborationStatus() != CollaborationStatus.OPEN) {
            throw new BadRequestException("This project is not open for collaboration");
        }
        
        if (collaboratorRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new BadRequestException("You are already a collaborator on this project");
        }
        
        if (requestRepository.existsByProjectIdAndRequesterId(projectId, userId)) {
            throw new BadRequestException("You have already requested to collaborate on this project");
        }
        
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        CollaborationRequest collabRequest = CollaborationRequest.builder()
                .project(project)
                .requester(requester)
                .requestedRole(request.getRequestedRole())
                .message(request.getMessage())
                .status(RequestStatus.PENDING)
                .build();
        
        collabRequest = requestRepository.save(collabRequest);
        
        // Send notification to project owner
        notificationService.sendCollaborationRequestNotification(project.getOwner().getId(), userId, projectId);
        
        return toRequestDTO(collabRequest);
    }
    
    @Override
    public CollaborationRequestDTO respondToCollaborationRequest(Long requestId, Long userId, CollaborationRequestResponse response) {
        CollaborationRequest collabRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("CollaborationRequest", "id", requestId));
        
        if (!collabRequest.getProject().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to respond to this request");
        }
        
        if (collabRequest.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("This request has already been processed");
        }
        
        if (response.getAccept()) {
            collabRequest.setStatus(RequestStatus.ACCEPTED);
            
            // Add as collaborator
            ProjectCollaborator collaborator = ProjectCollaborator.builder()
                    .project(collabRequest.getProject())
                    .user(collabRequest.getRequester())
                    .role(collabRequest.getRequestedRole())
                    .isOwner(false)
                    .build();
            collaboratorRepository.save(collaborator);
            
            // Send acceptance notification
            notificationService.sendCollaborationAcceptedNotification(
                    collabRequest.getRequester().getId(), 
                    collabRequest.getProject().getId());
        } else {
            collabRequest.setStatus(RequestStatus.REJECTED);
            
            // Send rejection notification
            notificationService.sendCollaborationRejectedNotification(
                    collabRequest.getRequester().getId(), 
                    collabRequest.getProject().getId());
        }
        
        collabRequest.setResponseMessage(response.getResponseMessage());
        collabRequest = requestRepository.save(collabRequest);
        
        return toRequestDTO(collabRequest);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CollaborationRequestDTO> getMyCollaborationRequests(Long userId, Pageable pageable) {
        return requestRepository.findByRequesterId(userId, pageable)
                .map(this::toRequestDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CollaborationRequestDTO> getRequestsForMyProjects(Long userId, Pageable pageable) {
        return requestRepository.findByProjectOwnerIdAndStatus(userId, RequestStatus.PENDING, pageable)
                .map(this::toRequestDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CollaborationRequestDTO> getRequestsForProject(Long projectId, Long userId, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to view these requests");
        }
        
        return requestRepository.findByProjectId(projectId, pageable)
                .map(this::toRequestDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CollaboratorDTO> getProjectCollaborators(Long projectId) {
        return collaboratorRepository.findByProjectId(projectId).stream()
                .map(this::toCollaboratorDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void removeCollaborator(Long projectId, Long collaboratorId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to remove collaborators");
        }
        
        ProjectCollaborator collaborator = collaboratorRepository.findByProjectIdAndUserId(projectId, collaboratorId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator", "userId", collaboratorId));
        
        if (collaborator.getIsOwner()) {
            throw new BadRequestException("Cannot remove the project owner");
        }
        
        collaboratorRepository.delete(collaborator);
    }
    
    @Override
    public ProjectDTO addProjectImage(Long projectId, Long userId, MultipartFile file, String caption) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to add images to this project");
        }
        
        String imageUrl = cloudinaryService.uploadImage(file, "project-images");
        
        int displayOrder = project.getImages().size();
        ProjectImage image = ProjectImage.builder()
                .project(project)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .caption(caption)
                .build();
        
        imageRepository.save(image);
        
        return toDTO(project, userId);
    }
    
    @Override
    public void removeProjectImage(Long projectId, Long imageId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to remove images from this project");
        }
        
        ProjectImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectImage", "id", imageId));
        
        if (!image.getProject().getId().equals(projectId)) {
            throw new BadRequestException("Image does not belong to this project");
        }
        
        cloudinaryService.deleteImage(image.getImageUrl());
        imageRepository.delete(image);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalProjectsCount() {
        return projectRepository.countAllProjects();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalCollaborationsCount() {
        return requestRepository.countAcceptedCollaborations();
    }
    
    private String generateSlug(String title) {
        String nowhitespace = WHITESPACE.matcher(title).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH);
        
        String baseSlug = slug;
        int counter = 1;
        while (projectRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private ProjectDTO toDTO(Project project, Long currentUserId) {
        DeveloperProfile ownerProfile = profileRepository
                .findByUserId(project.getOwner().getId())
                .orElse(null);

        int collaboratorsCount = collaboratorRepository.countByProjectId(project.getId());

        boolean isCollaborator = currentUserId != null &&
                collaboratorRepository.existsByProjectIdAndUserId(project.getId(), currentUserId);

        boolean hasRequestedCollaboration = currentUserId != null &&
                requestRepository.existsByProjectIdAndRequesterId(project.getId(), currentUserId);

        List<ProjectImageDTO> images = imageRepository
                .findByProjectIdOrderByDisplayOrder(project.getId())
                .stream()
                .map(img -> ProjectImageDTO.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .caption(img.getCaption())
                        .build())
                .collect(Collectors.toList());

        List<CollaboratorDTO> collaborators = collaboratorRepository
                .findByProjectId(project.getId())
                .stream()
                .map(this::toCollaboratorDTO)
                .collect(Collectors.toList());

        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .slug(project.getSlug())
                .shortDescription(project.getShortDescription())
                .detailedDescription(project.getDetailedDescription())
                .category(project.getCategory())
                .projectStage(project.getProjectStage())
                .collaborationStatus(project.getCollaborationStatus())
                .collaboratorsNeeded(project.getCollaboratorsNeeded())
                .isVisible(project.getIsVisible())
                .ownerId(project.getOwner().getId())
                .ownerName(project.getOwner().getFullName())
                .ownerUsername(project.getOwner().getUsername())
                .ownerProfilePhotoUrl(ownerProfile != null ? ownerProfile.getProfilePhotoUrl() : null)
                .githubRepoUrl(project.getGithubRepoUrl())
                .liveDemoUrl(project.getLiveDemoUrl())
                .documentationUrl(project.getDocumentationUrl())
                .videoDemoUrl(project.getVideoDemoUrl())
                .websiteUrl(project.getWebsiteUrl())

                //  IMPORTANT FIX: copy lazy collections into normal Java collections
                .techStack(project.getTechStack() != null ? new HashSet<>(project.getTechStack()) : new HashSet<>())
                .rolesNeeded(project.getRolesNeeded() != null ? new HashSet<>(project.getRolesNeeded()) : new HashSet<>())

                .images(images)
                .collaborators(collaborators)
                .viewsCount(project.getViewsCount())
                .collaboratorsCount(collaboratorsCount)
                .isCollaborator(isCollaborator)
                .hasRequestedCollaboration(hasRequestedCollaboration)
                .createdAt(project.getCreatedAt())
                .build();
    }
    
    private CollaboratorDTO toCollaboratorDTO(ProjectCollaborator collaborator) {
        DeveloperProfile profile = profileRepository.findByUserId(collaborator.getUser().getId()).orElse(null);
        
        return CollaboratorDTO.builder()
                .id(collaborator.getId())
                .userId(collaborator.getUser().getId())
                .username(collaborator.getUser().getUsername())
                .fullName(collaborator.getUser().getFullName())
                .profilePhotoUrl(profile != null ? profile.getProfilePhotoUrl() : null)
                .role(collaborator.getRole())
                .isOwner(collaborator.getIsOwner())
                .build();
    }
    
    private CollaborationRequestDTO toRequestDTO(CollaborationRequest request) {
        DeveloperProfile requesterProfile = profileRepository.findByUserId(request.getRequester().getId()).orElse(null);
        
        return CollaborationRequestDTO.builder()
                .id(request.getId())
                .projectId(request.getProject().getId())
                .projectTitle(request.getProject().getTitle())
                .requesterId(request.getRequester().getId())
                .requesterName(request.getRequester().getFullName())
                .requesterUsername(request.getRequester().getUsername())
                .requesterProfilePhotoUrl(requesterProfile != null ? requesterProfile.getProfilePhotoUrl() : null)
                .requestedRole(request.getRequestedRole())
                .message(request.getMessage())
                .status(request.getStatus())
                .responseMessage(request.getResponseMessage())
                .createdAt(request.getCreatedAt())
                .build();
    }
}