package com.buildwithus.upload.controller;

import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.upload.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "File upload APIs")
@SecurityRequirement(name = "bearerAuth")
public class UploadController {
    
    private final CloudinaryService cloudinaryService;
    
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder) {
        String imageUrl = cloudinaryService.uploadImage(file, folder);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", 
                Map.of("url", imageUrl)));
    }
}