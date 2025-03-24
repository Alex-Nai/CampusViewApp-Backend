package com.example.campusbackend.repository;

import com.example.campusbackend.entity.Booking;
import com.example.campusbackend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByResourceId(Long resourceId);
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    List<Booking> findByResourceIdAndStatus(Long resourceId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.resource.id = :resourceId AND " +
           "((b.startTime <= :endTime AND b.endTime >= :startTime) OR " +
           "(b.startTime >= :startTime AND b.startTime <= :endTime)) AND " +
           "b.status NOT IN ('REJECTED', 'CANCELLED')")
    List<Booking> findConflictingBookings(Long resourceId, LocalDateTime startTime, LocalDateTime endTime);
} 