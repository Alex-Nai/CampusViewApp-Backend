package com.example.campusbackend.dto;

import com.example.campusbackend.entity.ResourceType;
import lombok.Data;

@Data
public class ResourceDto {
    private Long id;
    private String name;
    private String description;
    private ResourceType type;
    private String location;
    private boolean available;
} 