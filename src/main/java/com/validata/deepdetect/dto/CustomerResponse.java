package com.validata.deepdetect.dto;

import java.time.LocalDateTime;

public record CustomerResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String address,
    String signatureUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 