package com.buildwithus.hackathon.dto;

import com.buildwithus.common.enums.PrimaryRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHackathonRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @Size(max = 200, message = "Organizer must be less than 200 characters")
    private String organizer;
    
    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;
    
    @URL(message = "Invalid registration URL")
    private String registrationLink;
    
    private LocalDate eventDate;
    
    private LocalDate endDate;
    
    @Size(max = 200, message = "Location must be less than 200 characters")
    private String location;
    
    private Boolean isOnline;
    
    private Integer teamSize;
    
    @Size(max = 2000, message = "Prize info must be less than 2000 characters")
    private String prizeInfo;
    
    private Set<PrimaryRole> requiredRoles;
    
    private Set<String> tags;
}