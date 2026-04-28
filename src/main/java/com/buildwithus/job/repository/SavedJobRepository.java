package com.buildwithus.job.repository;

import com.buildwithus.job.entity.Job;
import com.buildwithus.job.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
    
    Optional<SavedJob> findByUserIdAndJobId(Long userId, Long jobId);
    
    @Query("SELECT s.job FROM SavedJob s WHERE s.user.id = :userId AND s.isDeleted = false")
    Page<Job> findSavedJobsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    void deleteByUserIdAndJobId(Long userId, Long jobId);
}