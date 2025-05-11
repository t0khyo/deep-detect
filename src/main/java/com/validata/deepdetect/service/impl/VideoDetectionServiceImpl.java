package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.validata.deepdetect.dto.VideoDetectionResponse;
import com.validata.deepdetect.exception.InvalidFileException;
import com.validata.deepdetect.exception.ModelServerException;
import com.validata.deepdetect.service.VideoDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoDetectionServiceImpl implements VideoDetectionService {
    @Value("${model.server.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREDICT_ENDPOINT = "/api/video/predict";
    private static final String FIELD_VIDEO = "video";

    @Override
    public VideoDetectionResponse analyzeVideo(MultipartFile videoFile) {
        validateFile(videoFile);

        String url = baseUrl + PREDICT_ENDPOINT;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(FIELD_VIDEO, createFileResource(videoFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("Sending video analysis request to {} with file: {}", url, videoFile.getOriginalFilename());

            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (rawResponse.getStatusCode() == HttpStatus.OK && rawResponse.getBody() != null) {
                String responseBody = rawResponse.getBody();
                log.info("Raw model server response: {}", responseBody);
                
                // Parse the response as JsonNode to inspect the structure
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                // Check if the response contains error information
                if (rootNode.has("error")) {
                    String errorMessage = rootNode.get("error").asText();
                    log.error("Model server returned error: {}", errorMessage);
                    throw new ModelServerException("Model server error: " + errorMessage);
                }

                // Convert to VideoDetectionResponse
                VideoDetectionResponse response = objectMapper.readValue(responseBody, VideoDetectionResponse.class);
                
                log.info("Video analysis completed successfully for file: {}. Confidence: {}, IsAuthentic: {}, Type: {}",
                        videoFile.getOriginalFilename(),
                        response.confidence(),
                        response.isAuthentic(),
                        response.detectedType());

                return response;
            } else {
                String errorMessage = "Model server returned unexpected response: " + rawResponse.getStatusCode();
                log.error(errorMessage);
                throw new ModelServerException(errorMessage);
            }
        } catch (RestClientException e) {
            log.error("Error during video analysis request to model server", e);
            throw new ModelServerException("Error during video analysis: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error parsing model server response", e);
            throw new ModelServerException("Error parsing model server response: " + e.getMessage(), e);
        }
    }

    private ByteArrayResource createFileResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new ModelServerException("Error reading video file: " + file.getOriginalFilename(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Video file cannot be null or empty");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new InvalidFileException("Invalid file type. Expected video file, got: " + contentType);
        }
        
        // Validate file size (e.g., max 100MB)
        long maxSize = 100 * 1024 * 1024; // 100MB in bytes
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("Video file size exceeds maximum limit of 100MB");
        }
    }
} 