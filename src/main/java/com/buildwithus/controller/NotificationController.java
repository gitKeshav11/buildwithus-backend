package com.buildwithus.controller;

import com.buildwithus.entity.Notification;
import com.buildwithus.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository repo;

    public NotificationController(NotificationRepository repo){
        this.repo = repo;
    }

    @GetMapping
    public List<Notification> getAll(){
        return repo.findAll();
    }

    @PostMapping
    public Notification create(@RequestBody Notification n){
        return repo.save(n);
    }
}