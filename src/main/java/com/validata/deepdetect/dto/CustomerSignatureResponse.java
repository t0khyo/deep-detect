package com.validata.deepdetect.dto;

import lombok.Builder;

@Builder
public record CustomerSignatureResponse(
        String firstName,
        String lastName,
        String signatureUrl) {
}
