package com.buildwithus.job.repository;

import com.buildwithus.common.enums.JobType;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.WorkMode;
import com.buildwithus.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.isDeleted = false ORDER BY j.createdAt DESC")
    Page<Job> findAllActive(Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.isDeleted = false AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Job> searchJobs(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.isDeleted = false AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:roleCategory IS NULL OR j.roleCategory = :roleCategory) AND " +
           "(:workMode IS NULL OR j.workMode = :workMode) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<Job> findByFilters(
            @Param("jobType") JobType jobType,
            @Param("roleCategory") PrimaryRole roleCategory,
            @Param("workMode") WorkMode workMode,
            @Param("location") String location,
            Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.isFeatured = true AND j.isDeleted = false")
    Page<Job> findFeaturedJobs(Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.postedBy.id = :userId AND j.isDeleted = false")
    Page<Job> findByPostedBy(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.isActive = true AND j.isDeleted = false")
    long countActiveJobs();
}