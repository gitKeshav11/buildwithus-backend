package com.buildwithus.controller;

import com.buildwithus.entity.DeveloperProfile;
import com.buildwithus.service.DeveloperProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class DeveloperProfileController {

    private final DeveloperProfileService service;

    public DeveloperProfileController(DeveloperProfileService service) {
        this.service = service;
    }

    @PostMapping
    public DeveloperProfile createProfile(@RequestBody DeveloperProfile profile){
        return service.saveProfile(profile);
    }

    @GetMapping
    public List<DeveloperProfile> getAll(){
        return service.getAll();
    }

    @GetMapping("/search")
    public List<DeveloperProfile> searchBySkill(@RequestParam String skill){
        return service.findBySkill(skill);
    }
}