package com.buildwithus.hackathon.repository;

import com.buildwithus.hackathon.entity.Hackathon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    
    @Query("SELECT h FROM Hackathon h WHERE h.isActive = true AND h.isDeleted = false ORDER BY h.eventDate ASC")
    Page<Hackathon> findAllActive(Pageable pageable);
    
    @Query("SELECT h FROM Hackathon h WHERE h.isActive = true AND h.isDeleted = false AND " +
           "(LOWER(h.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.organizer) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Hackathon> searchHackathons(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT h FROM Hackathon h WHERE h.postedBy.id = :userId AND h.isDeleted = false")
    Page<Hackathon> findByPostedBy(@Param("userId") Long userId, Pageable pageable);
}