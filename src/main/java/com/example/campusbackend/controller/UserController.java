package com.example.campusbackend.controller;

import com.example.campusbackend.dto.UserDto;
import com.example.campusbackend.dto.UserRegistrationDto;
import com.example.campusbackend.dto.UserUpdateDto;
import com.example.campusbackend.model.LoginResponse;
import com.example.campusbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        return ResponseEntity.ok(userService.registerUser(registrationDto));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto updateDto) {
        return ResponseEntity.ok(userService.updateUser(userId, updateDto));
    }

    @GetMapping("/info/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LoginResponse> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @PutMapping("/{userId}/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LoginResponse> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto updateDto) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, updateDto));
    }

    @PostMapping("/{userId}/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LoginResponse> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("avatar") MultipartFile avatar) {
        return ResponseEntity.ok(userService.uploadAvatar(userId, avatar));
    }
} 