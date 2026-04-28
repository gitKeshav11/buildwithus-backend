package com.buildwithus.project.dto;

import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationRequestDTO {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private Long requesterId;
    private String requesterName;
    private String requesterUsername;
    private String requesterProfilePhotoUrl;
    private PrimaryRole requestedRole;
    private String message;
    private RequestStatus status;
    private String responseMessage;
    private LocalDateTime createdAt;
}