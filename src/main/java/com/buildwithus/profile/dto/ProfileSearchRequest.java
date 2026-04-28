package com.buildwithus.profile.dto;

import com.buildwithus.common.enums.AvailabilityStatus;
import com.buildwithus.common.enums.ExperienceLevel;
import com.buildwithus.common.enums.PrimaryRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSearchRequest {
    private String keyword;
    private PrimaryRole role;
    private ExperienceLevel experienceLevel;
    private AvailabilityStatus availabilityStatus;
    private String location;
    private Boolean isVerified;
}