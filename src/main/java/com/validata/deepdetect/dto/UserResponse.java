package com.validata.deepdetect.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        String firstName,
        String lastName,
        String email
) {
}
