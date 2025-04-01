package com.example.campusbackend.repository;

import com.example.campusbackend.entity.Resource;
import com.example.campusbackend.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByType(ResourceType type);
    List<Resource> findByTypeNot(ResourceType type);
    List<Resource> findByAvailable(boolean available);
    List<Resource> findByTypeAndAvailable(ResourceType type, boolean available);

    @Query("SELECT r FROM Resource r WHERE r.type != 'CLASSROOM'")
    List<Resource> findNonClassroomResources();

    @Query("SELECT r FROM Resource r WHERE r.id NOT IN " +
           "(SELECT b.resource.id FROM Booking b WHERE " +
           "((b.startTime <= :endTime AND b.endTime >= :startTime) OR " +
           "(b.startTime >= :startTime AND b.startTime <= :endTime)) AND " +
           "b.status NOT IN ('REJECTED', 'CANCELLED'))")
    List<Resource> findAvailableForTimeSlot(LocalDateTime startTime, LocalDateTime endTime);
} 