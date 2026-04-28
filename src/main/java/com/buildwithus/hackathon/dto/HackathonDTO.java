package com.buildwithus.hackathon.dto;

import com.buildwithus.common.enums.PrimaryRole;
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
public class HackathonDTO {
    private Long id;
    private String title;
    private String organizer;
    private String description;
    private String registrationLink;
    private LocalDate eventDate;
    private LocalDate endDate;
    private String location;
    private Boolean isOnline;
    private Integer teamSize;
    private String prizeInfo;
    private Boolean isActive;
    private Long postedById;
    private String postedByName;
    private Set<PrimaryRole> requiredRoles;
    private Set<String> tags;
    private LocalDateTime createdAt;
}