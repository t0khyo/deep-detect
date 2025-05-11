package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.SignatureVerificationResponse;
import com.validata.deepdetect.service.SignatureDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/signature")
@RequiredArgsConstructor
@Tag(name = "Signature Detection", description = "APIs for signature detection and verification")
public class SignatureDetectionController {

    private final SignatureDetectionService signatureDetectionService;

    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Verify a signature", description = "Verifies if a signature matches a genuine signature")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signature verification successful",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SignatureVerificationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SignatureVerificationResponse> verifySignature(
            @RequestParam("genuineSignature") MultipartFile genuineSignature,
            @RequestParam("signatureToVerify") MultipartFile signatureToVerify) {
        SignatureVerificationResponse response = signatureDetectionService.verifySignature(genuineSignature, signatureToVerify);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/{customerId}/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Verify a customer's signature", description = "Verifies if a customer's signature matches their original signature")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signature verification successful",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SignatureVerificationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SignatureVerificationResponse> verifyCustomerSignature(
            @PathVariable String customerId,
            @RequestParam("signatureToVerify") MultipartFile signatureToVerify) {
        SignatureVerificationResponse response = signatureDetectionService.verifyCustomerSignature(customerId, signatureToVerify);
        return ResponseEntity.ok(response);
    }
}
