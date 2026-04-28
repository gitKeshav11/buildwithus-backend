package com.buildwithus.ai.repository;

import com.buildwithus.ai.entity.ChatConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    
    @Query("SELECT c FROM ChatConversation c WHERE c.user.id = :userId AND c.isDeleted = false ORDER BY c.updatedAt DESC")
    Page<ChatConversation> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM ChatConversation c WHERE c.isDeleted = false")
    long countAllConversations();
}