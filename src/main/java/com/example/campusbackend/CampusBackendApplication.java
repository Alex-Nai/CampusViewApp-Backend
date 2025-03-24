package com.example.campusbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CampusBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusBackendApplication.class, args);
    }
} 