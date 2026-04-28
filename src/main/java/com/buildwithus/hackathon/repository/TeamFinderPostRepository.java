package com.buildwithus.hackathon.repository;

import com.buildwithus.common.enums.TeamFinderType;
import com.buildwithus.hackathon.entity.TeamFinderPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamFinderPostRepository extends JpaRepository<TeamFinderPost, Long> {
    
    @Query("SELECT t FROM TeamFinderPost t WHERE t.isActive = true AND t.isDeleted = false ORDER BY t.createdAt DESC")
    Page<TeamFinderPost> findAllActive(Pageable pageable);
    
    @Query("SELECT t FROM TeamFinderPost t WHERE t.isActive = true AND t.isDeleted = false AND t.postType = :type")
    Page<TeamFinderPost> findByType(@Param("type") TeamFinderType type, Pageable pageable);
    
    @Query("SELECT t FROM TeamFinderPost t WHERE t.user.id = :userId AND t.isDeleted = false")
    Page<TeamFinderPost> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT t FROM TeamFinderPost t WHERE t.hackathon.id = :hackathonId AND t.isActive = true AND t.isDeleted = false")
    Page<TeamFinderPost> findByHackathonId(@Param("hackathonId") Long hackathonId, Pageable pageable);
}