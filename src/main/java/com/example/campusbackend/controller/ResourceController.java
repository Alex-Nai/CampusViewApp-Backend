package com.example.campusbackend.controller;

import com.example.campusbackend.dto.ResourceCreateDto;
import com.example.campusbackend.dto.ResourceDto;
import com.example.campusbackend.dto.ResourceUpdateDto;
import com.example.campusbackend.entity.ResourceType;
import com.example.campusbackend.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<List<ResourceDto>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/other")
    public ResponseEntity<List<ResourceDto>> getOtherResources() {
        return ResponseEntity.ok(resourceService.getOtherResources());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> getResource(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResourceDto>> getResourcesByType(@PathVariable ResourceType type) {
        return ResponseEntity.ok(resourceService.getResourcesByType(type));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ResourceDto>> getAvailableResources() {
        return ResponseEntity.ok(resourceService.getAvailableResources());
    }

    @GetMapping("/available/type/{type}")
    public ResponseEntity<List<ResourceDto>> getAvailableResourcesByType(@PathVariable ResourceType type) {
        return ResponseEntity.ok(resourceService.getAvailableResourcesByType(type));
    }

    @GetMapping("/available/timeslot")
    public ResponseEntity<List<ResourceDto>> getAvailableResourcesForTimeSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(resourceService.getAvailableResourcesForTimeSlot(startTime, endTime));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceDto> createResource(@RequestBody ResourceCreateDto createDto) {
        return ResponseEntity.ok(resourceService.createResource(
            createDto.getName(),
            createDto.getDescription(),
            createDto.getType(),
            createDto.getLocation()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceDto> updateResource(
            @PathVariable Long id,
            @RequestBody ResourceUpdateDto updateDto) {
        return ResponseEntity.ok(resourceService.updateResource(
            id,
            updateDto.getName(),
            updateDto.getDescription(),
            updateDto.getType(),
            updateDto.getLocation(),
            updateDto.isAvailable()
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok().build();
    }
} 