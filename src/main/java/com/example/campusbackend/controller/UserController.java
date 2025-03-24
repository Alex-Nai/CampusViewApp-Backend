package com.example.campusbackend.controller;

import com.example.campusbackend.dto.UserDto;
import com.example.campusbackend.dto.UserRegistrationDto;
import com.example.campusbackend.dto.UserUpdateDto;
import com.example.campusbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserRegistrationDto registrationDto) {
        return ResponseEntity.ok(userService.createUser(
            registrationDto.getUsername(),
            registrationDto.getPassword(),
            registrationDto.getEmail(),
            registrationDto.getFullName()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDto updateDto) {
        return ResponseEntity.ok(userService.updateUser(
            id,
            updateDto.getEmail(),
            updateDto.getFullName()
        ));
    }
} 