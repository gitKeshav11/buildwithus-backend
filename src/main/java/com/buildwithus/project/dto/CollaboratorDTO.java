package com.buildwithus.project.dto;

import com.buildwithus.common.enums.PrimaryRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String profilePhotoUrl;
    private PrimaryRole role;
    private Boolean isOwner;
}