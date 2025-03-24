package com.example.campusbackend.controller;

import com.example.campusbackend.dto.AuthResponse;
import com.example.campusbackend.dto.UserDto;
import com.example.campusbackend.dto.UserLoginDto;
import com.example.campusbackend.dto.UserRegistrationDto;
import com.example.campusbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegistrationDto registrationDto) {
        return ResponseEntity.ok(authService.register(registrationDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }
} 