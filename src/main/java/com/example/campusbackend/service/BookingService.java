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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
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
        logger.info("开始创建预约 - 用户ID: {}, 资源ID: {}, 开始时间: {}, 结束时间: {}, 用途: {}", 
                   userId, resourceId, startTime, endTime, purpose);

        try {
            // 查找用户
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("用户不存在 - 用户ID: {}", userId);
                        return new RuntimeException("User not found");
                    });
            logger.info("找到用户: {}", user.getUsername());

            // 查找资源
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> {
                        logger.error("资源不存在 - 资源ID: {}", resourceId);
                        return new RuntimeException("Resource not found");
                    });
            logger.info("找到资源: {}", resource.getName());

            // 检查资源是否可用
            if (!resource.isAvailable()) {
                logger.error("资源不可用 - 资源ID: {}", resourceId);
                throw new RuntimeException("Resource is not available");
            }

            // 检查时间有效性
            if (startTime.isAfter(endTime)) {
                logger.error("开始时间晚于结束时间 - 开始时间: {}, 结束时间: {}", startTime, endTime);
                throw new RuntimeException("Start time must be before end time");
            }

            if (startTime.isBefore(LocalDateTime.now())) {
                logger.error("预约时间早于当前时间 - 开始时间: {}, 当前时间: {}", startTime, LocalDateTime.now());
                throw new RuntimeException("Cannot book in the past");
            }

            // 检查时间冲突
            List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                    resourceId, startTime, endTime);
            if (!conflictingBookings.isEmpty()) {
                logger.error("存在时间冲突的预约 - 资源ID: {}, 开始时间: {}, 结束时间: {}", 
                           resourceId, startTime, endTime);
                throw new RuntimeException("Resource is already booked for this time slot");
            }

            // 创建预约
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setResource(resource);
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);
            booking.setPurpose(purpose);
            booking.setStatus(BookingStatus.PENDING);

            // 保存预约
            Booking savedBooking = bookingRepository.save(booking);
            logger.info("预约创建成功 - 预约ID: {}", savedBooking.getId());

            return toDto(savedBooking);
        } catch (Exception e) {
            logger.error("创建预约失败", e);
            throw e;
        }
    }

    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, BookingStatus status) {
        logger.info("更新预约状态 - 预约ID: {}, 新状态: {}", bookingId, status);
        
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> {
                        logger.error("预约不存在 - 预约ID: {}", bookingId);
                        return new RuntimeException("Booking not found");
                    });

            booking.setStatus(status);
            Booking updatedBooking = bookingRepository.save(booking);
            logger.info("预约状态更新成功 - 预约ID: {}", updatedBooking.getId());
            
            return toDto(updatedBooking);
        } catch (Exception e) {
            logger.error("更新预约状态失败", e);
            throw e;
        }
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        logger.info("取消预约 - 预约ID: {}, 用户ID: {}", bookingId, userId);
        
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> {
                        logger.error("预约不存在 - 预约ID: {}", bookingId);
                        return new RuntimeException("Booking not found");
                    });

            if (!booking.getUser().getId().equals(userId)) {
                logger.error("用户无权取消该预约 - 预约ID: {}, 用户ID: {}", bookingId, userId);
                throw new RuntimeException("Not authorized to cancel this booking");
            }

            if (booking.getStartTime().isBefore(LocalDateTime.now())) {
                logger.error("无法取消过去的预约 - 预约ID: {}, 开始时间: {}", bookingId, booking.getStartTime());
                throw new RuntimeException("Cannot cancel past bookings");
            }

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            logger.info("预约取消成功 - 预约ID: {}", bookingId);
        } catch (Exception e) {
            logger.error("取消预约失败", e);
            throw e;
        }
    }

    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setResourceId(booking.getResource().getId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setPurpose(booking.getPurpose());
        return dto;
    }
} 