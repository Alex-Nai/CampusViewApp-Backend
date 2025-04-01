package com.example.campusbackend.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String email;
    private String realName;
    private String studentId;
    private String phone;
} 