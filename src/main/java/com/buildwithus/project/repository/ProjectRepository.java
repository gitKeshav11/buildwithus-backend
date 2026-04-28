package com.buildwithus.project.repository;

import com.buildwithus.common.enums.CollaborationStatus;
import com.buildwithus.common.enums.ProjectCategory;
import com.buildwithus.common.enums.ProjectStage;
import com.buildwithus.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Optional<Project> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT p FROM Project p WHERE p.isVisible = true AND p.isDeleted = false")
    Page<Project> findAllVisible(Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.isVisible = true AND p.isDeleted = false AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.detailedDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Project> searchProjects(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.isVisible = true AND p.isDeleted = false AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:stage IS NULL OR p.projectStage = :stage) AND " +
           "(:status IS NULL OR p.collaborationStatus = :status)")
    Page<Project> findByFilters(
            @Param("category") ProjectCategory category,
            @Param("stage") ProjectStage stage,
            @Param("status") CollaborationStatus status,
            Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.owner.id = :userId AND p.isDeleted = false")
    Page<Project> findByOwnerId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.collaborationStatus = 'OPEN' AND p.isVisible = true AND p.isDeleted = false")
    Page<Project> findOpenForCollaboration(Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.isDeleted = false")
    long countAllProjects();
}