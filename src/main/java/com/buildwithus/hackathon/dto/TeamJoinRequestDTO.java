package com.buildwithus.hackathon.dto;

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
public class TeamJoinRequestDTO {
    private Long id;
    private Long postId;
    private String postTitle;
    private Long requesterId;
    private String requesterName;
    private String requesterUsername;
    private String requesterProfilePhotoUrl;
    private String message;
    private RequestStatus status;
    private String responseMessage;
    private LocalDateTime createdAt;
}