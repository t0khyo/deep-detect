package com.validata.deepdetect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record VideoDetectionResponse(
        Float confidence,
        @JsonProperty("is_real")
        Boolean isAuthentic,
        @JsonProperty("prediction")
        String detectedType
) {
} 