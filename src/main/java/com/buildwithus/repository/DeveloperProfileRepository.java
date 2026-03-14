package com.buildwithus.repository;

import com.buildwithus.entity.DeveloperProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfile,Long> {

    List<DeveloperProfile> findBySkillsContaining(String skill);

}