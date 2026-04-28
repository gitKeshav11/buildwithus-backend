package com.buildwithus.profile.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tech_stacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechStack {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String category;
    
    @Column(name = "icon_url")
    private String iconUrl;
}