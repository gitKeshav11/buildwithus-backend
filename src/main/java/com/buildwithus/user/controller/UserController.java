package com.buildwithus.user.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import com.buildwithus.user.dto.UserDTO;
import com.buildwithus.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(summary = "Get current user")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return userService.findById(currentUser.getId())
                .map(user -> ResponseEntity.ok(ApiResponse.success(userService.toDTO(user))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pagedUsers = userService.getAllUsers(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(pagedUsers)));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pagedUsers = userService.searchUsers(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(pagedUsers)));
    }
    
    @PostMapping("/{userId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block a user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> blockUser(@PathVariable Long userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User blocked successfully"));
    }
    
    @PostMapping("/{userId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unblock a user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@PathVariable Long userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User unblocked successfully"));
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}