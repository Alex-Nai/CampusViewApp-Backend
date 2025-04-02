package com.example.campusbackend.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;
    private String email;
    private String phone;
    private String studentId;
    private String department;
    private String avatar;
} 