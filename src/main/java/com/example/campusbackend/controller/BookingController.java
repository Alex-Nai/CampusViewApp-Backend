package com.example.campusbackend.controller;

import com.example.campusbackend.dto.BookingCreateDto;
import com.example.campusbackend.dto.BookingDto;
import com.example.campusbackend.dto.BookingUpdateDto;
import com.example.campusbackend.entity.BookingStatus;
import com.example.campusbackend.entity.User;
import com.example.campusbackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    private final BookingService bookingService;

    @GetMapping("/my")
    public ResponseEntity<List<BookingDto>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("获取用户预约列表 - 用户: {}", userDetails.getUsername());
        try {
            User user = (User) userDetails;
            return ResponseEntity.ok(bookingService.getBookingsByUser(user.getId()));
        } catch (Exception e) {
            logger.error("获取用户预约列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<BookingDto>> getBookingsByResource(@PathVariable Long resourceId) {
        logger.info("获取资源预约列表 - 资源ID: {}", resourceId);
        try {
            return ResponseEntity.ok(bookingService.getBookingsByResource(resourceId));
        } catch (Exception e) {
            logger.error("获取资源预约列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BookingCreateDto request) {
        logger.info("创建预约请求 - 用户: {}, 资源ID: {}, 开始时间: {}, 结束时间: {}, 用途: {}", 
                   userDetails.getUsername(), request.getResourceId(), 
                   request.getStartTime(), request.getEndTime(), request.getPurpose());
        
        try {
            User user = (User) userDetails;
            BookingDto booking = bookingService.createBooking(
                    user.getId(),
                    request.getResourceId(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getPurpose()
            );
            logger.info("预约创建成功 - 预约ID: {}", booking.getId());
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            logger.error("创建预约失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("创建预约时发生未知错误", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{bookingId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status) {
        logger.info("更新预约状态 - 预约ID: {}, 新状态: {}", bookingId, status);
        try {
            return ResponseEntity.ok(bookingService.updateBookingStatus(bookingId, status));
        } catch (RuntimeException e) {
            logger.error("更新预约状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("更新预约状态时发生未知错误", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookingId) {
        logger.info("取消预约请求 - 用户: {}, 预约ID: {}", userDetails.getUsername(), bookingId);
        try {
            User user = (User) userDetails;
            bookingService.cancelBooking(bookingId, user.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("取消预约失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("取消预约时发生未知错误", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 