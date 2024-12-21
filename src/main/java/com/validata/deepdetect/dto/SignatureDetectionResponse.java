package com.validata.deepdetect.dto;

import lombok.Builder;

@Builder
public record SignatureDetectionResponse(
        Float similarity,
        Float probability,
        Boolean signatureWasNotForged
) {
}
