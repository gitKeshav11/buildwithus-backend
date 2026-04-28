package com.buildwithus.job.dto;

import com.buildwithus.common.enums.JobType;
import com.buildwithus.common.enums.PrimaryRole;
import com.buildwithus.common.enums.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private Long id;
    private String title;
    private String companyName;
    private String companyLogoUrl;
    private String description;
    private JobType jobType;
    private PrimaryRole roleCategory;
    private WorkMode workMode;
    private String location;
    private String salaryRange;
    private String applyLink;
    private LocalDate lastDate;
    private Long postedById;
    private String postedByName;
    private Boolean isActive;
    private Boolean isFeatured;
    private Set<String> tags;
    private Integer viewsCount;
    private Integer clicksCount;
    private Boolean isSaved;
    private LocalDateTime createdAt;
}