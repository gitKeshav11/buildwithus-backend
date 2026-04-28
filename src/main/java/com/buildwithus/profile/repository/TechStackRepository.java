package com.buildwithus.profile.repository;

import com.buildwithus.profile.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    Optional<TechStack> findByNameIgnoreCase(String name);
    List<TechStack> findByCategory(String category);
    List<TechStack> findByNameContainingIgnoreCase(String name);
}