package com.buildwithus.upload.service;

import com.buildwithus.exception.BadRequestException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public String uploadImage(MultipartFile file, String folder) {
        validateImage(file);

        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "buildwithus/" + sanitizeFolder(folder),
                    "resource_type", "image",
                    "transformation", List.of(
                            ObjectUtils.asMap("quality", "auto"),
                            ObjectUtils.asMap("fetch_format", "auto")
                    )
            );

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            Object secureUrl = result.get("secure_url");

            if (secureUrl == null) {
                throw new BadRequestException("Image upload failed. Cloudinary URL missing.");
            }

            return secureUrl.toString();

        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new BadRequestException("Failed to upload image. Please try again.");
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);

            if (publicId != null && !publicId.isBlank()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Deleted image from Cloudinary: {}", publicId);
            }
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", imageUrl, e);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 10MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BadRequestException("Invalid file type. Allowed types: JPEG, PNG, GIF, WebP");
        }
    }

    private String sanitizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "general";
        }

        return folder
                .replaceAll("[^a-zA-Z0-9/_-]", "")
                .replaceAll("/{2,}", "/");
    }

    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/upload/");

            if (parts.length < 2) {
                return null;
            }

            String path = parts[1];

            if (path.startsWith("v")) {
                int slashIndex = path.indexOf("/");
                if (slashIndex > 0) {
                    path = path.substring(slashIndex + 1);
                }
            }

            int dotIndex = path.lastIndexOf(".");
            if (dotIndex > 0) {
                path = path.substring(0, dotIndex);
            }

            return path;

        } catch (Exception e) {
            log.error("Failed to extract public ID from URL: {}", imageUrl, e);
            return null;
        }
    }
}