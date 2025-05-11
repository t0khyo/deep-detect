package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.VideoDetectionResponse;
import com.validata.deepdetect.service.VideoDetectionService;
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
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
@Tag(name = "Video Detection", description = "APIs for video authenticity analysis")
public class VideoDetectionController {
    private final VideoDetectionService videoDetectionService;

    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Analyze video authenticity",
        description = "Analyzes a video file to determine its authenticity and detect any manipulations"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Video analysis completed successfully",
            content = @Content(schema = @Schema(implementation = VideoDetectionResponse.class))
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
    public ResponseEntity<VideoDetectionResponse> analyzeVideo(
            @RequestParam("video") MultipartFile videoFile) {
        return ResponseEntity.ok(videoDetectionService.analyzeVideo(videoFile));
    }
} 