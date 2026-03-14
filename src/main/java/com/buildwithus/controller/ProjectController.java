package com.buildwithus.controller;

import com.buildwithus.entity.Project;
import com.buildwithus.service.ProjectService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping
    public Project create(@RequestBody Project project){
        return projectService.createProject(project);
    }

    @GetMapping
    public List<Project> all(){
        return projectService.getAllProjects();
    }
}