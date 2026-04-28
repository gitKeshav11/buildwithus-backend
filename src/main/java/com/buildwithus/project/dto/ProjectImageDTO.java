package com.buildwithus.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectImageDTO {
    private Long id;
    private String imageUrl;
    private Integer displayOrder;
    private String caption;
}