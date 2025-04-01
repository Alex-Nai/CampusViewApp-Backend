package com.example.campusbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
public class BookingCreateDto {
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    private String purpose;

    public void setStartTime(String startTime) {
        try {
            this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("开始时间格式不正确，应为ISO格式（如：2024-03-20T10:00:00）");
        }
    }

    public void setEndTime(String endTime) {
        try {
            this.endTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("结束时间格式不正确，应为ISO格式（如：2024-03-20T10:00:00）");
        }
    }
} 