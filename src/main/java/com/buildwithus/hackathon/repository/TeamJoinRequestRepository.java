package com.buildwithus.hackathon.repository;

import com.buildwithus.common.enums.RequestStatus;
import com.buildwithus.hackathon.entity.TeamJoinRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
    
    boolean existsByPostIdAndRequesterId(Long postId, Long requesterId);
    
    Optional<TeamJoinRequest> findByPostIdAndRequesterId(Long postId, Long requesterId);
    
    @Query("SELECT t FROM TeamJoinRequest t WHERE t.requester.id = :userId AND t.isDeleted = false")
    Page<TeamJoinRequest> findByRequesterId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT t FROM TeamJoinRequest t WHERE t.post.id = :postId AND t.isDeleted = false")
    Page<TeamJoinRequest> findByPostId(@Param("postId") Long postId, Pageable pageable);
    
    @Query("SELECT t FROM TeamJoinRequest t WHERE t.post.user.id = :userId AND t.status = :status AND t.isDeleted = false")
    Page<TeamJoinRequest> findByPostOwnerIdAndStatus(
            @Param("userId") Long userId, 
            @Param("status") RequestStatus status, 
            Pageable pageable);
}