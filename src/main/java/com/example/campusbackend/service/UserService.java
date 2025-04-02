package com.example.campusbackend.service;

import com.example.campusbackend.dto.UserDto;
import com.example.campusbackend.dto.UserRegistrationDto;
import com.example.campusbackend.dto.UserUpdateDto;
import com.example.campusbackend.model.LoginResponse;
import com.example.campusbackend.entity.User;
import com.example.campusbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

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
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setRealName(registrationDto.getRealName());
        user.setStudentId(registrationDto.getStudentId());
        user.setPhone(registrationDto.getPhone());
        user.setDepartment(registrationDto.getDepartment());
        return toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public LoginResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToLoginResponse(user);
    }

    @Transactional
    public LoginResponse updateUserProfile(Long userId, UserUpdateDto updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新基本信息
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getRealName() != null) {
            user.setRealName(updateDto.getRealName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }

        // 更新密码
        if (updateDto.getPassword() != null && updateDto.getNewPassword() != null) {
            if (!passwordEncoder.matches(updateDto.getPassword(), user.getPassword())) {
                throw new RuntimeException("当前密码错误");
            }
            user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
        }

        user = userRepository.save(user);
        return convertToLoginResponse(user);
    }

    @Transactional
    public LoginResponse uploadAvatar(Long userId, MultipartFile avatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        try {
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成文件名
            String originalFilename = avatar.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(avatar.getInputStream(), filePath);

            // 更新用户头像
            user.setAvatar(filename);
            user = userRepository.save(user);

            return convertToLoginResponse(user);
        } catch (IOException e) {
            throw new RuntimeException("上传头像失败", e);
        }
    }

    @Transactional
    public UserDto updateUser(Long userId, UserUpdateDto updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getRealName() != null) {
            user.setRealName(updateDto.getRealName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getStudentId() != null) {
            user.setStudentId(updateDto.getStudentId());
        }
        if (updateDto.getDepartment() != null) {
            user.setDepartment(updateDto.getDepartment());
        }

        return toDto(userRepository.save(user));
    }

    private LoginResponse convertToLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRoles().get(0)); // 获取第一个角色
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setStudentId(user.getStudentId());
        response.setDepartment(user.getDepartment());
        response.setAvatar(user.getAvatar());
        return response;
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRealName(user.getRealName());
        dto.setStudentId(user.getStudentId());
        dto.setPhone(user.getPhone());
        dto.setDepartment(user.getDepartment());
        dto.setAvatar(user.getAvatar());
        return dto;
    }
} 