package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.service.SignatureDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/signature")
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
        return ResponseEntity.ok(detectionService.verifySignature(genuineSignature, signatureToVerify));
    }

}
