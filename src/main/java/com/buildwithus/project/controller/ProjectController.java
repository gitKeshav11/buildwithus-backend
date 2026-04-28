package com.buildwithus.project.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.common.enums.CollaborationStatus;
import com.buildwithus.common.enums.ProjectCategory;
import com.buildwithus.common.enums.ProjectStage;
import com.buildwithus.project.dto.*;
import com.buildwithus.project.service.ProjectService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management and collaboration APIs")
public class ProjectController {
    
    private final ProjectService projectService;
    
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectDTO project = projectService.createProject(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", project));
    }
    
    @PutMapping("/{projectId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a project")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable Long projectId,
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectDTO project = projectService.updateProject(projectId, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", project));
    }
    
    @DeleteMapping("/{projectId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a project")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long projectId,
            @CurrentUser UserPrincipal currentUser) {
        projectService.deleteProject(projectId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully"));
    }
    
    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(
            @PathVariable Long projectId,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        ProjectDTO project = projectService.getProjectById(projectId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
    
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get project by slug")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectBySlug(
            @PathVariable String slug,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        ProjectDTO project = projectService.getProjectBySlug(slug, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
    
    @GetMapping
    @Operation(summary = "Get all projects")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectDTO>>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var projects = projectService.getAllProjects(PageRequest.of(page, size, sort), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(projects)));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search projects")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectDTO>>> searchProjects(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var projects = projectService.searchProjects(keyword, PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(projects)));
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filter projects")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectDTO>>> filterProjects(
            @RequestParam(required = false) ProjectCategory category,
            @RequestParam(required = false) ProjectStage projectStage,
            @RequestParam(required = false) CollaborationStatus collaborationStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        ProjectSearchRequest request = ProjectSearchRequest.builder()
                .category(category)
                .projectStage(projectStage)
                .collaborationStatus(collaborationStatus)
                .build();
        var projects = projectService.filterProjects(request, PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(projects)));
    }
    
    @GetMapping("/my-projects")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my projects")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectDTO>>> getMyProjects(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var projects = projectService.getMyProjects(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(projects)));
    }
    
    @GetMapping("/open-collaboration")
    @Operation(summary = "Get projects open for collaboration")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectDTO>>> getOpenForCollaboration(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserPrincipal currentUser) {
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        var projects = projectService.getOpenForCollaboration(PageRequest.of(page, size), currentUserId);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(projects)));
    }
    
    @GetMapping("/collaborating")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get projects I'm collaborating on")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectDTO>>> getCollaboratingProjects(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var projects = projectService.getProjectsAsCollaborator(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(projects)));
    }
    
    // Collaboration endpoints
    @PostMapping("/{projectId}/collaborate")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Request to collaborate on a project")
    public ResponseEntity<ApiResponse<CollaborationRequestDTO>> requestCollaboration(
            @PathVariable Long projectId,
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CollaborationRequestCreate request) {
        CollaborationRequestDTO result = projectService.requestCollaboration(projectId, currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Collaboration request sent", result));
    }
    
    @PostMapping("/collaboration-requests/{requestId}/respond")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Respond to a collaboration request")
    public ResponseEntity<ApiResponse<CollaborationRequestDTO>> respondToRequest(
            @PathVariable Long requestId,
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CollaborationRequestResponse response) {
        CollaborationRequestDTO result = projectService.respondToCollaborationRequest(requestId, currentUser.getId(), response);
        return ResponseEntity.ok(ApiResponse.success("Response recorded", result));
    }
    
    @GetMapping("/my-collaboration-requests")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my collaboration requests")
    public ResponseEntity<ApiResponse<PagedResponse<CollaborationRequestDTO>>> getMyCollaborationRequests(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var requests = projectService.getMyCollaborationRequests(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(requests)));
    }
    
    @GetMapping("/requests-for-my-projects")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get collaboration requests for my projects")
    public ResponseEntity<ApiResponse<PagedResponse<CollaborationRequestDTO>>> getRequestsForMyProjects(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var requests = projectService.getRequestsForMyProjects(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(requests)));
    }
    
    @GetMapping("/{projectId}/requests")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get collaboration requests for a specific project")
    public ResponseEntity<ApiResponse<PagedResponse<CollaborationRequestDTO>>> getRequestsForProject(
            @PathVariable Long projectId,
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var requests = projectService.getRequestsForProject(projectId, currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(requests)));
    }
    
    @GetMapping("/{projectId}/collaborators")
    @Operation(summary = "Get project collaborators")
    public ResponseEntity<ApiResponse<List<CollaboratorDTO>>> getProjectCollaborators(@PathVariable Long projectId) {
        List<CollaboratorDTO> collaborators = projectService.getProjectCollaborators(projectId);
        return ResponseEntity.ok(ApiResponse.success(collaborators));
    }
    
    @DeleteMapping("/{projectId}/collaborators/{collaboratorId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove a collaborator from project")
    public ResponseEntity<ApiResponse<Void>> removeCollaborator(
            @PathVariable Long projectId,
            @PathVariable Long collaboratorId,
            @CurrentUser UserPrincipal currentUser) {
        projectService.removeCollaborator(projectId, collaboratorId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Collaborator removed"));
    }
    
    // Image endpoints
    @PostMapping(value = "/{projectId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add image to project")
    public ResponseEntity<ApiResponse<ProjectDTO>> addProjectImage(
            @PathVariable Long projectId,
            @CurrentUser UserPrincipal currentUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String caption) {
        ProjectDTO project = projectService.addProjectImage(projectId, currentUser.getId(), file, caption);
        return ResponseEntity.ok(ApiResponse.success("Image added", project));
    }
    
    @DeleteMapping("/{projectId}/images/{imageId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove image from project")
    public ResponseEntity<ApiResponse<Void>> removeProjectImage(
            @PathVariable Long projectId,
            @PathVariable Long imageId,
            @CurrentUser UserPrincipal currentUser) {
        projectService.removeProjectImage(projectId, imageId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Image removed"));
    }
}