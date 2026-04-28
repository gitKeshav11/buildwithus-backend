package com.buildwithus.profile.repository;

import com.buildwithus.common.enums.AvailabilityStatus;
import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.profile.entity.DeveloperProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfile, Long> {
    
    Optional<DeveloperProfile> findByUserId(Long userId);
    
    Optional<DeveloperProfile> findByUsername(String username);
    
    Optional<DeveloperProfile> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT p FROM DeveloperProfile p WHERE p.profileVisibility = true AND p.isDeleted = false")
    Page<DeveloperProfile> findAllPublic(Pageable pageable);
    
    @Query("SELECT p FROM DeveloperProfile p WHERE p.profileVisibility = true AND p.isDeleted = false AND " +
           "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.headline) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.bio) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<DeveloperProfile> searchProfiles(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM DeveloperProfile p WHERE p.profileVisibility = true AND p.isDeleted = false AND " +
           "(:role IS NULL OR p.primaryRole = :role) AND " +
           "(:level IS NULL OR p.experienceLevel = :level) AND " +
           "(:status IS NULL OR p.availabilityStatus = :status) AND " +
           "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<DeveloperProfile> findByFilters(
            @Param("role") PrimaryRole role,
            @Param("level") ExperienceLevel level,
            @Param("status") AvailabilityStatus status,
            @Param("location") String location,
            Pageable pageable);
    
    @Query("SELECT p FROM DeveloperProfile p WHERE p.isVerified = true AND p.profileVisibility = true AND p.isDeleted = false")
    Page<DeveloperProfile> findVerifiedDevelopers(Pageable pageable);
}