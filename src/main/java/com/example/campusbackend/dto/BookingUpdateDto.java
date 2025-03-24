package com.example.campusbackend.dto;

import com.example.campusbackend.entity.BookingStatus;
import lombok.Data;

@Data
public class BookingUpdateDto {
    private BookingStatus status;
    private String purpose;
} 