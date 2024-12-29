package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.service.SignatureDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/v1/signature")
@RequiredArgsConstructor
public class SignatureDetectionController {
    private final SignatureDetectionService detectionService;

    @PostMapping("/verify")
    @Operation(summary = "Verify signature authenticity", description = "Validates a signature against a genuine reference.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SignatureDetectionResponse> verifySignature(
            @RequestParam MultipartFile genuineSignature,
            @RequestParam MultipartFile signatureToVerify
    ) {
        log.info("verifySignature(): genuine: {} - to verify: {}", genuineSignature.getOriginalFilename(), signatureToVerify.getOriginalFilename());
        return ResponseEntity.ok(detectionService.verifySignature(genuineSignature, signatureToVerify));
    }

    @PostMapping("/verify/test")
    @Operation(summary = "Verify signature authenticity", description = "Validates a signature against a genuine reference.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SignatureDetectionResponse> verifySignatureTest(
            MultipartFile genuineSignature,
            MultipartFile signatureToVerify
    ) {
        Random random = new Random();

        boolean isAuthentic = random.nextBoolean();

        float confidenceScore1 = 50 + random.nextFloat() * 50; // Genuine signature confidence score
        float confidenceScore2 = 50 + random.nextFloat() * 50; // Signature to verify confidence score

        if (isAuthentic) {
            confidenceScore1 = 80 + random.nextFloat() * 20; // Higher confidence for genuine signature
            confidenceScore2 = 70 + random.nextFloat() * 30; // Higher confidence for signature to verify
        } else {
            confidenceScore1 = 50 + random.nextFloat() * 30; // Lower confidence for genuine signature
            confidenceScore2 = 30 + random.nextFloat() * 30; // Lower confidence for signature to verify
        }

        return ResponseEntity.ok(new SignatureDetectionResponse(confidenceScore1, confidenceScore2, isAuthentic));
    }


}
