package com.example.campusbackend.controller;

import com.example.campusbackend.dto.BookingCreateDto;
import com.example.campusbackend.dto.BookingDto;
import com.example.campusbackend.dto.BookingUpdateDto;
import com.example.campusbackend.entity.BookingStatus;
import com.example.campusbackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/my")
    public ResponseEntity<List<BookingDto>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(Long.parseLong(userDetails.getUsername())));
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<BookingDto>> getBookingsByResource(@PathVariable Long resourceId) {
        return ResponseEntity.ok(bookingService.getBookingsByResource(resourceId));
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BookingCreateDto createDto) {
        return ResponseEntity.ok(bookingService.createBooking(
            Long.parseLong(userDetails.getUsername()),
            createDto.getResourceId(),
            createDto.getStartTime(),
            createDto.getEndTime(),
            createDto.getPurpose()
        ));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody BookingUpdateDto updateDto) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, updateDto.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        bookingService.cancelBooking(id, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }
} 