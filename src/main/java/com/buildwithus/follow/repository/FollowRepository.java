package com.buildwithus.follow.repository;

import com.buildwithus.follow.entity.Follow;
import com.buildwithus.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId AND f.isDeleted = false")
    Page<User> findFollowingByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId AND f.isDeleted = false")
    Page<User> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following.id = :userId AND f.isDeleted = false")
    int countByFollowingId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId AND f.isDeleted = false")
    int countByFollowerId(@Param("userId") Long userId);
    
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}