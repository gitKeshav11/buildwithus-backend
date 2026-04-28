package com.buildwithus.job.service;

import com.buildwithus.job.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {
    JobDTO createJob(Long userId, CreateJobRequest request);
    JobDTO updateJob(Long jobId, UpdateJobRequest request, Long userId);
    void deleteJob(Long jobId, Long userId);
    JobDTO getJobById(Long jobId, Long currentUserId);
    Page<JobDTO> getAllJobs(Pageable pageable, Long currentUserId);
    Page<JobDTO> searchJobs(String keyword, Pageable pageable, Long currentUserId);
    Page<JobDTO> filterJobs(JobSearchRequest request, Pageable pageable, Long currentUserId);
    Page<JobDTO> getFeaturedJobs(Pageable pageable, Long currentUserId);
    Page<JobDTO> getMyPostedJobs(Long userId, Pageable pageable);
    void saveJob(Long userId, Long jobId);
    void unsaveJob(Long userId, Long jobId);
    Page<JobDTO> getSavedJobs(Long userId, Pageable pageable);
    void trackJobClick(Long jobId);
    long getTotalJobsCount();
}