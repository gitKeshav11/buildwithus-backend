package com.buildwithus.leaderboard.repository;

import com.buildwithus.leaderboard.entity.LeaderboardPoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaderboardPointsRepository extends JpaRepository<LeaderboardPoints, Long> {
    Optional<LeaderboardPoints> findByUserId(Long userId);
    
    @Query("SELECT l FROM LeaderboardPoints l WHERE l.isDeleted = false ORDER BY l.totalPoints DESC")
    Page<LeaderboardPoints> findTopDevelopers(Pageable pageable);
    
    @Query("SELECT COUNT(l) + 1 FROM LeaderboardPoints l WHERE l.totalPoints > " +
           "(SELECT COALESCE(lp.totalPoints, 0) FROM LeaderboardPoints lp WHERE lp.user.id = :userId)")
    int getUserRank(@Param("userId") Long userId);
}