package com.buildwithus.verification.repository;

import com.buildwithus.common.enums.VerificationStatus;
import com.buildwithus.verification.entity.VerificationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {
    
    Optional<VerificationRequest> findByUserIdAndStatus(Long userId, VerificationStatus status);
    
    Optional<VerificationRequest> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT v FROM VerificationRequest v WHERE v.status = :status AND v.isDeleted = false ORDER BY v.createdAt ASC")
    Page<VerificationRequest> findByStatus(@Param("status") VerificationStatus status, Pageable pageable);
    
    boolean existsByUserIdAndStatus(Long userId, VerificationStatus status);
}