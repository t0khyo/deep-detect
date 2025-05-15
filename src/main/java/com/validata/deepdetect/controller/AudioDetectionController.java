package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.AudioDetectionResponse;
import com.validata.deepdetect.service.AudioDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/audio")
@RequiredArgsConstructor
@Tag(name = "Audio Detection", description = "APIs for audio authenticity analysis")
public class AudioDetectionController {
    private final AudioDetectionService audioDetectionService;

    @PostMapping(value = "/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Analyze audio authenticity",
        description = "Analyzes an audio file (WAV or MP3) to determine if it's real or fake"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Audio analysis completed successfully",
            content = @Content(schema = @Schema(implementation = AudioDetectionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - file is missing, empty, or invalid format"
        ),
        @ApiResponse(
            responseCode = "413",
            description = "File size exceeds maximum limit"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error or model server error"
        )
    })
    public ResponseEntity<AudioDetectionResponse> predictAudio(
            @RequestParam("audio") MultipartFile audioFile) {
        return ResponseEntity.ok(audioDetectionService.predictAudio(audioFile));
    }
} 