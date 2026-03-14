package com.buildwithus.service;

import com.buildwithus.entity.DeveloperProfile;
import com.buildwithus.repository.DeveloperProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeveloperProfileService {

    private final DeveloperProfileRepository repository;

    public DeveloperProfileService(DeveloperProfileRepository repository) {
        this.repository = repository;
    }

    public DeveloperProfile saveProfile(DeveloperProfile profile){
        return repository.save(profile);
    }

    public List<DeveloperProfile> findBySkill(String skill){
        return repository.findBySkillsContaining(skill);
    }

    public List<DeveloperProfile> getAll(){
        return repository.findAll();
    }
}