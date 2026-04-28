package com.buildwithus.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechStackDTO {
    private Long id;
    private String name;
    private String category;
    private String iconUrl;
}