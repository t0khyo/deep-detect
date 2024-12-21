package com.validata.deepdetect.dto;

import lombok.Builder;

@Builder
public record SignatureDetectionResponse(
        Float similarityPercentage,
        Float probabilityPercentage,
        Boolean signatureWasNotForged
) {
}
