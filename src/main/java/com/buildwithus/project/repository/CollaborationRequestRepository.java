package com.buildwithus.project.repository;

import com.buildwithus.common.enums.RequestStatus;
import com.buildwithus.project.entity.CollaborationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaborationRequestRepository extends JpaRepository<CollaborationRequest, Long> {
    
    boolean existsByProjectIdAndRequesterId(Long projectId, Long requesterId);
    
    Optional<CollaborationRequest> findByProjectIdAndRequesterId(Long projectId, Long requesterId);
    
    @Query("SELECT cr FROM CollaborationRequest cr WHERE cr.requester.id = :userId AND cr.isDeleted = false")
    Page<CollaborationRequest> findByRequesterId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT cr FROM CollaborationRequest cr WHERE cr.project.id = :projectId AND cr.isDeleted = false")
    Page<CollaborationRequest> findByProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    @Query("SELECT cr FROM CollaborationRequest cr WHERE cr.project.owner.id = :ownerId AND cr.status = :status AND cr.isDeleted = false")
    Page<CollaborationRequest> findByProjectOwnerIdAndStatus(
            @Param("ownerId") Long ownerId, 
            @Param("status") RequestStatus status, 
            Pageable pageable);
    
    @Query("SELECT COUNT(cr) FROM CollaborationRequest cr WHERE cr.status = 'ACCEPTED' AND cr.isDeleted = false")
    long countAcceptedCollaborations();
}