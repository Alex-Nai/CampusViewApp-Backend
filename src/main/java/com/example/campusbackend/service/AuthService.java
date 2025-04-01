package com.example.campusbackend.service;

import com.example.campusbackend.dto.AuthResponse;
import com.example.campusbackend.dto.UserDto;
import com.example.campusbackend.dto.UserLoginDto;
import com.example.campusbackend.dto.UserRegistrationDto;
import com.example.campusbackend.entity.User;
import com.example.campusbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserDto register(UserRegistrationDto registrationDto) {
        log.info("Attempting to register user: {}", registrationDto.getUsername());
        
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        if (userRepository.existsByStudentId(registrationDto.getStudentId())) {
            throw new RuntimeException("学号已存在");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(registrationDto.getPassword()); // 直接使用明文密码
        user.setRealName(registrationDto.getRealName());
        user.setStudentId(registrationDto.getStudentId());
        user.setPhone(registrationDto.getPhone());
        user.setEmail(registrationDto.getEmail());

        user = userRepository.save(user);
        log.info("Successfully registered user: {}", user.getUsername());
        return toDto(user);
    }

    public AuthResponse login(UserLoginDto loginDto) {
        log.info("Attempting to login user: {}", loginDto.getUsername());
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()
                )
            );
            
            User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

            String token = jwtService.generateToken(user);
            log.info("Successfully logged in user: {}", user.getUsername());
            return new AuthResponse(token, toDto(user));
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginDto.getUsername(), e);
            throw new RuntimeException("用户名或密码错误");
        }
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .studentId(user.getStudentId())
            .phone(user.getPhone())
            .email(user.getEmail())
            .role("admin001".equals(user.getStudentId()) ? "ADMIN" : "USER")
            .build();
    }
} 