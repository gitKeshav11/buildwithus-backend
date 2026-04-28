package com.buildwithus.project.service;

import com.buildwithus.project.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(Long userId, CreateProjectRequest request);
    ProjectDTO updateProject(Long projectId, UpdateProjectRequest request, Long userId);
    void deleteProject(Long projectId, Long userId);
    ProjectDTO getProjectById(Long projectId, Long currentUserId);
    ProjectDTO getProjectBySlug(String slug, Long currentUserId);
    Page<ProjectDTO> getAllProjects(Pageable pageable, Long currentUserId);
    Page<ProjectDTO> searchProjects(String keyword, Pageable pageable, Long currentUserId);
    Page<ProjectDTO> filterProjects(ProjectSearchRequest request, Pageable pageable, Long currentUserId);
    Page<ProjectDTO> getMyProjects(Long userId, Pageable pageable);
    Page<ProjectDTO> getOpenForCollaboration(Pageable pageable, Long currentUserId);
    Page<ProjectDTO> getProjectsAsCollaborator(Long userId, Pageable pageable);
    
    // Collaboration
    CollaborationRequestDTO requestCollaboration(Long projectId, Long userId, CollaborationRequestCreate request);
    CollaborationRequestDTO respondToCollaborationRequest(Long requestId, Long userId, CollaborationRequestResponse response);
    Page<CollaborationRequestDTO> getMyCollaborationRequests(Long userId, Pageable pageable);
    Page<CollaborationRequestDTO> getRequestsForMyProjects(Long userId, Pageable pageable);
    Page<CollaborationRequestDTO> getRequestsForProject(Long projectId, Long userId, Pageable pageable);
    List<CollaboratorDTO> getProjectCollaborators(Long projectId);
    void removeCollaborator(Long projectId, Long collaboratorId, Long userId);
    
    // Images
    ProjectDTO addProjectImage(Long projectId, Long userId, MultipartFile file, String caption);
    void removeProjectImage(Long projectId, Long imageId, Long userId);
    
    long getTotalProjectsCount();
    long getTotalCollaborationsCount();
}