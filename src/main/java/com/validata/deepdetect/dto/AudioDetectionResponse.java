package com.validata.deepdetect.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for audio authenticity prediction")
public class AudioDetectionResponse {
    @Schema(description = "Prediction result indicating if the audio is real or fake", 
            example = "Real audio", 
            allowableValues = {"Real audio", "Fake audio"})
    private String prediction;
} 