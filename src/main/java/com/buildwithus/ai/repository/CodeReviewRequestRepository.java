package com.buildwithus.ai.repository;

import com.buildwithus.ai.entity.CodeReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeReviewRequestRepository extends JpaRepository<CodeReviewRequest, Long> {
    
    @Query("SELECT c FROM CodeReviewRequest c WHERE c.user.id = :userId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<CodeReviewRequest> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM CodeReviewRequest c WHERE c.isDeleted = false")
    long countAllReviews();
}