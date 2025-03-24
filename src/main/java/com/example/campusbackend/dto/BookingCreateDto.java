package com.example.campusbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
} 