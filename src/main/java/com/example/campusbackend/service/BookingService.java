package com.example.campusbackend.service;

import com.example.campusbackend.dto.BookingDto;
import com.example.campusbackend.entity.Booking;
import com.example.campusbackend.entity.BookingStatus;
import com.example.campusbackend.entity.Resource;
import com.example.campusbackend.entity.User;
import com.example.campusbackend.repository.BookingRepository;
import com.example.campusbackend.repository.ResourceRepository;
import com.example.campusbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByResource(Long resourceId) {
        return bookingRepository.findByResourceId(resourceId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto createBooking(Long userId, Long resourceId, LocalDateTime startTime, 
                                  LocalDateTime endTime, String purpose) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (!resource.isAvailable()) {
            throw new RuntimeException("Resource is not available");
        }

        if (startTime.isAfter(endTime)) {
            throw new RuntimeException("Start time must be before end time");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book in the past");
        }

        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                resourceId, startTime, endTime);
        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Resource is already booked for this time slot");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);
        booking.setStatus(BookingStatus.PENDING);

        return toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        return toDto(bookingRepository.save(booking));
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this booking");
        }

        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel past bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUsername(booking.getUser().getUsername());
        dto.setResourceId(booking.getResource().getId());
        dto.setResourceName(booking.getResource().getName());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setPurpose(booking.getPurpose());
        return dto;
    }
} 