package com.buildwithus.project.repository;

import com.buildwithus.project.entity.ProjectCollaborator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectCollaboratorRepository extends JpaRepository<ProjectCollaborator, Long> {
    
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
    
    Optional<ProjectCollaborator> findByProjectIdAndUserId(Long projectId, Long userId);
    
    List<ProjectCollaborator> findByProjectId(Long projectId);
    
    @Query("SELECT pc.project FROM ProjectCollaborator pc WHERE pc.user.id = :userId AND pc.isDeleted = false")
    Page<com.buildwithus.project.entity.Project> findProjectsByCollaboratorId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(pc) FROM ProjectCollaborator pc WHERE pc.project.id = :projectId AND pc.isDeleted = false")
    int countByProjectId(@Param("projectId") Long projectId);
}