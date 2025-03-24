package com.example.campusbackend.dto;

import com.example.campusbackend.entity.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
} 