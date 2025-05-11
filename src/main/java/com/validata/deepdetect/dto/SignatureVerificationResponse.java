package com.validata.deepdetect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureVerificationResponse {
    private double similarityPercentage;
    private double probabilityPercentage;
    private boolean signatureWasNotForged;
} 