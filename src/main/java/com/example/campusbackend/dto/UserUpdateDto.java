package com.example.campusbackend.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String email;
    private String realName;
    private String phone;
    private String studentId;
    private String department;
    private String password;
    private String newPassword;
} 