package com.validata.deepdetect.mapper;

import com.validata.deepdetect.dto.GoogleUserInfo;
import com.validata.deepdetect.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class GoogleUserMapper {

    public User toUser(GoogleUserInfo dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .firstName(dto.getName())
                .lastName(extractLastName(dto.getName()))
                .email(dto.getEmail())
                .password("")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Collections.emptyList())
                .build();
    }
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "Unknown";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "Unknown";
    }

}
