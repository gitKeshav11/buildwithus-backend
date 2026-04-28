package com.buildwithus.job.service.impl;

import com.buildwithus.exception.BadRequestException;
import com.buildwithus.exception.ForbiddenException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.job.dto.*;
import com.buildwithus.job.entity.Job;
import com.buildwithus.job.entity.SavedJob;
import com.buildwithus.job.repository.JobRepository;
import com.buildwithus.job.repository.SavedJobRepository;
import com.buildwithus.job.service.JobService;
import com.buildwithus.user.entity.Role;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobServiceImpl implements JobService {
    
    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    
    @Override
    public JobDTO createJob(Long userId, CreateJobRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Only admin or company can create jobs
        if (!user.hasRole(Role.ADMIN) && !user.hasRole(Role.COMPANY)) {
            throw new ForbiddenException("You don't have permission to create jobs");
        }
        
        Job job = Job.builder()
                .title(request.getTitle())
                .companyName(request.getCompanyName())
                .companyLogoUrl(request.getCompanyLogoUrl())
                .description(request.getDescription())
                .jobType(request.getJobType())
                .roleCategory(request.getRoleCategory())
                .workMode(request.getWorkMode())
                .location(request.getLocation())
                .salaryRange(request.getSalaryRange())
                .applyLink(request.getApplyLink())
                .lastDate(request.getLastDate())
                .postedBy(user)
                .isActive(true)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .tags(request.getTags() != null ? request.getTags() : new HashSet<>())
                .viewsCount(0)
                .clicksCount(0)
                .build();
        
        job = jobRepository.save(job);
        return toDTO(job, userId);
    }
    
    @Override
    public JobDTO updateJob(Long jobId, UpdateJobRequest request, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Only admin or the job poster can update
        if (!user.hasRole(Role.ADMIN) && !job.getPostedBy().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this job");
        }
        
        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getCompanyName() != null) job.setCompanyName(request.getCompanyName());
        if (request.getCompanyLogoUrl() != null) job.setCompanyLogoUrl(request.getCompanyLogoUrl());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getJobType() != null) job.setJobType(request.getJobType());
        if (request.getRoleCategory() != null) job.setRoleCategory(request.getRoleCategory());
        if (request.getWorkMode() != null) job.setWorkMode(request.getWorkMode());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getSalaryRange() != null) job.setSalaryRange(request.getSalaryRange());
        if (request.getApplyLink() != null) job.setApplyLink(request.getApplyLink());
        if (request.getLastDate() != null) job.setLastDate(request.getLastDate());
        if (request.getIsActive() != null) job.setIsActive(request.getIsActive());
        if (request.getIsFeatured() != null) job.setIsFeatured(request.getIsFeatured());
        if (request.getTags() != null) job.setTags(request.getTags());
        
        job = jobRepository.save(job);
        return toDTO(job, userId);
    }
    
    @Override
    public void deleteJob(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (!user.hasRole(Role.ADMIN) && !job.getPostedBy().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this job");
        }
        
        job.setIsDeleted(true);
        job.setIsActive(false);
        jobRepository.save(job);
    }
    
    @Override
    @Transactional(readOnly = true)
    public JobDTO getJobById(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        
        // Increment view count
        job.setViewsCount(job.getViewsCount() + 1);
        jobRepository.save(job);
        
        return toDTO(job, currentUserId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> getAllJobs(Pageable pageable, Long currentUserId) {
        return jobRepository.findAllActive(pageable)
                .map(job -> toDTO(job, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> searchJobs(String keyword, Pageable pageable, Long currentUserId) {
        return jobRepository.searchJobs(keyword, pageable)
                .map(job -> toDTO(job, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> filterJobs(JobSearchRequest request, Pageable pageable, Long currentUserId) {
        return jobRepository.findByFilters(
                request.getJobType(),
                request.getRoleCategory(),
                request.getWorkMode(),
                request.getLocation(),
                pageable
        ).map(job -> toDTO(job, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> getFeaturedJobs(Pageable pageable, Long currentUserId) {
        return jobRepository.findFeaturedJobs(pageable)
                .map(job -> toDTO(job, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> getMyPostedJobs(Long userId, Pageable pageable) {
        return jobRepository.findByPostedBy(userId, pageable)
                .map(job -> toDTO(job, userId));
    }
    
    @Override
    public void saveJob(Long userId, Long jobId) {
        if (savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new BadRequestException("Job is already saved");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        
        SavedJob savedJob = SavedJob.builder()
                .user(user)
                .job(job)
                .build();
        
        savedJobRepository.save(savedJob);
    }
    
    @Override
    public void unsaveJob(Long userId, Long jobId) {
        SavedJob savedJob = savedJobRepository.findByUserIdAndJobId(userId, jobId)
                .orElseThrow(() -> new BadRequestException("Job is not saved"));
        savedJobRepository.delete(savedJob);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> getSavedJobs(Long userId, Pageable pageable) {
        return savedJobRepository.findSavedJobsByUserId(userId, pageable)
                .map(job -> toDTO(job, userId));
    }
    
    @Override
    public void trackJobClick(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        job.setClicksCount(job.getClicksCount() + 1);
        jobRepository.save(job);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalJobsCount() {
        return jobRepository.countActiveJobs();
    }
    
    private JobDTO toDTO(Job job, Long currentUserId) {
        boolean isSaved = currentUserId != null && 
                savedJobRepository.existsByUserIdAndJobId(currentUserId, job.getId());
        
        return JobDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .companyLogoUrl(job.getCompanyLogoUrl())
                .description(job.getDescription())
                .jobType(job.getJobType())
                .roleCategory(job.getRoleCategory())
                .workMode(job.getWorkMode())
                .location(job.getLocation())
                .salaryRange(job.getSalaryRange())
                .applyLink(job.getApplyLink())
                .lastDate(job.getLastDate())
                .postedById(job.getPostedBy() != null ? job.getPostedBy().getId() : null)
                .postedByName(job.getPostedBy() != null ? job.getPostedBy().getFullName() : null)
                .isActive(job.getIsActive())
                .isFeatured(job.getIsFeatured())
                .tags(job.getTags())
                .viewsCount(job.getViewsCount())
                .clicksCount(job.getClicksCount())
                .isSaved(isSaved)
                .createdAt(job.getCreatedAt())
                .build();
    }
}