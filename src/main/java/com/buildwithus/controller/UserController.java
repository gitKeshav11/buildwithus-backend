package com.buildwithus.controller;

import com.buildwithus.dto.UserRequestDTO;
import com.buildwithus.dto.UserResponseDTO;
import com.buildwithus.entity.User;
import com.buildwithus.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO request){

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setSkills(request.getSkills());
        user.setGithubLink(request.getGithubLink());

        User savedUser = userService.register(user);

        UserResponseDTO response = userService.convertToDTO(savedUser);

        return ResponseEntity.ok(response);
    }

    // Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(){

        List<UserResponseDTO> users = userService.getAllUsers()
                .stream()
                .map(userService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // Get User By ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){

        User user = userService.getUserById(id);

        return ResponseEntity.ok(userService.convertToDTO(user));
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok("User deleted successfully");
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequestDTO request){

        String response = userService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(response);
    }
}