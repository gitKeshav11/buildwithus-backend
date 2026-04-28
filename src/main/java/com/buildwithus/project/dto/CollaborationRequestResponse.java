package com.buildwithus.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationRequestResponse {
    
    @NotNull(message = "Accept decision is required")
    private Boolean accept;
    
    @Size(max = 500, message = "Response message must be less than 500 characters")
    private String responseMessage;
}