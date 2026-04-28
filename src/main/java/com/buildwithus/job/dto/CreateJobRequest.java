package com.buildwithus.job.dto;

import com.buildwithus.common.enums.JobType;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateJobRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must be less than 200 characters")
    private String companyName;
    
    @URL(message = "Invalid company logo URL")
    private String companyLogoUrl;
    
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;
    
    @NotNull(message = "Job type is required")
    private JobType jobType;
    
    private PrimaryRole roleCategory;
    
    private WorkMode workMode;
    
    @Size(max = 200, message = "Location must be less than 200 characters")
    private String location;
    
    @Size(max = 100, message = "Salary range must be less than 100 characters")
    private String salaryRange;
    
    @NotBlank(message = "Apply link is required")
    @URL(message = "Invalid apply URL")
    private String applyLink;
    
    private LocalDate lastDate;
    
    private Boolean isFeatured;
    
    private Set<String> tags;
}