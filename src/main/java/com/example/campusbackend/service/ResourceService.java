package com.example.campusbackend.service;

import com.example.campusbackend.dto.ResourceDto;
import com.example.campusbackend.entity.Resource;
import com.example.campusbackend.entity.ResourceType;
import com.example.campusbackend.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<ResourceDto> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResourceDto getResourceById(Long id) {
        return resourceRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    @Transactional(readOnly = true)
    public List<ResourceDto> getResourcesByType(ResourceType type) {
        return resourceRepository.findByType(type).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceDto> getAvailableResources() {
        return resourceRepository.findByAvailable(true).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceDto> getAvailableResourcesByType(ResourceType type) {
        return resourceRepository.findByTypeAndAvailable(type, true).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResourceDto> getAvailableResourcesForTimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        return resourceRepository.findAvailableForTimeSlot(startTime, endTime).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResourceDto createResource(String name, String description, ResourceType type, String location) {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setDescription(description);
        resource.setType(type);
        resource.setLocation(location);
        resource.setAvailable(true);

        return toDto(resourceRepository.save(resource));
    }

    @Transactional
    public ResourceDto updateResource(Long id, String name, String description, ResourceType type, 
                                   String location, Boolean available) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (name != null) resource.setName(name);
        if (description != null) resource.setDescription(description);
        if (type != null) resource.setType(type);
        if (location != null) resource.setLocation(location);
        if (available != null) resource.setAvailable(available);

        return toDto(resourceRepository.save(resource));
    }

    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new RuntimeException("Resource not found");
        }
        resourceRepository.deleteById(id);
    }

    private ResourceDto toDto(Resource resource) {
        ResourceDto dto = new ResourceDto();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setDescription(resource.getDescription());
        dto.setType(resource.getType());
        dto.setLocation(resource.getLocation());
        dto.setAvailable(resource.isAvailable());
        return dto;
    }
} 