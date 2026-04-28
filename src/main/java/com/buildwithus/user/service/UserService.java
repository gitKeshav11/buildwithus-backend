package com.buildwithus.user.service;

import com.buildwithus.user.dto.UserDTO;
import com.buildwithus.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    User save(User user);
    UserDTO toDTO(User user);
    Page<UserDTO> getAllUsers(Pageable pageable);
    Page<UserDTO> searchUsers(String keyword, Pageable pageable);
    void blockUser(Long userId);
    void unblockUser(Long userId);
    void deleteUser(Long userId);
    long getTotalUsersCount();
}