package com.example.campusbackend.service;

import com.example.campusbackend.dto.UserDto;
import com.example.campusbackend.entity.User;
import com.example.campusbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Transactional
    public UserDto createUser(String username, String password, String email, String realName, String studentId, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        if (userRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("学号已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRealName(realName);
        user.setStudentId(studentId);
        user.setPhone(phone);

        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, String email, String realName, String phone) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("邮箱已存在");
            }
            user.setEmail(email);
        }

        if (realName != null) {
            user.setRealName(realName);
        }

        if (phone != null) {
            user.setPhone(phone);
        }

        return toDto(userRepository.save(user));
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRealName(user.getRealName());
        dto.setStudentId(user.getStudentId());
        dto.setPhone(user.getPhone());
        dto.setRole("admin001".equals(user.getStudentId()) ? "ADMIN" : "USER");
        return dto;
    }
} 