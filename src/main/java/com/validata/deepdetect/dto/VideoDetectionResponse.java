package com.validata.deepdetect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record VideoDetectionResponse(
        @JsonProperty("confidence")
        double confidence,

        @JsonProperty("is_real")
        boolean isReal,

        @JsonProperty("prediction")
        String prediction
) {
}