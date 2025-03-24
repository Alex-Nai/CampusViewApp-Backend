package com.example.campusbackend.dto;

import com.example.campusbackend.entity.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private Long userId;
    private String username;
    private Long resourceId;
    private String resourceName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private String purpose;
} 