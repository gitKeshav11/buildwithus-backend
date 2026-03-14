package com.buildwithus.service;

import com.buildwithus.entity.User;
import com.buildwithus.repository.UserRepository;
import com.buildwithus.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public User register(User user){
        return userRepository.save(user);
    }

    public String login(User user){

        User existingUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!existingUser.getPassword().equals(user.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(existingUser.getEmail());
    }
}