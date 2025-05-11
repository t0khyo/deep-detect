package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.service.SignatureDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/signature")
@RequiredArgsConstructor
@Tag(name = "Signature Detection", description = "APIs for signature verification and analysis")
public class SignatureDetectionController {
    private final SignatureDetectionService detectionService;

    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Verify signature authenticity",
        description = "Compares a signature against a genuine reference to determine its authenticity"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Signature verification completed successfully",
            content = @Content(schema = @Schema(implementation = SignatureDetectionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - files are missing, empty, or invalid format",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Missing Files",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "Both genuine signature and signature to verify are required",
                                "path": "/api/v1/signature/verify"
                            }"""
                    ),
                    @ExampleObject(
                        name = "Invalid File Format",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "Invalid file type. Expected image file, got: application/pdf",
                                "path": "/api/v1/signature/verify"
                            }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "413",
            description = "File size exceeds maximum limit",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "File Too Large",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 413,
                                "message": "File size exceeds maximum limit of 5MB",
                                "path": "/api/v1/signature/verify"
                            }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error or model server error",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Model Server Error",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 500,
                                "message": "Error during signature analysis: Model server unavailable",
                                "path": "/api/v1/signature/verify"
                            }"""
                    ),
                    @ExampleObject(
                        name = "Processing Error",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 500,
                                "message": "Error processing signature files: Invalid image format",
                                "path": "/api/v1/signature/verify"
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<SignatureDetectionResponse> verifySignature(
            @RequestParam("genuineSignature") MultipartFile genuineSignature,
            @RequestParam("signatureToVerify") MultipartFile signatureToVerify
    ) {
        return ResponseEntity.ok(detectionService.verifySignature(genuineSignature, signatureToVerify));
    }
}
