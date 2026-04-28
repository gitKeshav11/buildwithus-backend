package com.buildwithus.project.dto;

import com.buildwithus.common.enums.PrimaryRole;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationRequestCreate {
    
    private PrimaryRole requestedRole;
    
    @Size(max = 1000, message = "Message must be less than 1000 characters")
    private String message;
}