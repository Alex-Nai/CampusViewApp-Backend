package com.example.campusbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(min = 2, max = 50, message = "姓名长度必须在2-50个字符之间")
    private String realName;

    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    private String newPassword;

    @Size(min = 11, max = 11, message = "手机号必须是11位")
    private String phone;
} 