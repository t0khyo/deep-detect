package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.AudioDetectionResponse;
import com.validata.deepdetect.service.AudioDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioDetectionServiceImpl implements AudioDetectionService {
    @Value("${model.server.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREDICT_ENDPOINT = "/api/audio/predict";
    private static final String FIELD_AUDIO = "audio";

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "audio/wav",
            "audio/wave",
            "audio/x-wav",
            "audio/mpeg",
            "audio/mp3"
    );

    @Override
    public AudioDetectionResponse predictAudio(MultipartFile audioFile) {
        validateAudioFile(audioFile);
        
        try {
            // Create headers for multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create the multipart request body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            };
            body.add(FIELD_AUDIO, resource);

            // Create the request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Make the API call to the model server
            String url = baseUrl + PREDICT_ENDPOINT;
            ResponseEntity<AudioDetectionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    AudioDetectionResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Invalid response from model server");
            }
        } catch (IOException e) {
            log.error("Error reading audio file", e);
            throw new RuntimeException("Failed to process audio file", e);
        } catch (Exception e) {
            log.error("Error calling model server", e);
            throw new RuntimeException("Failed to get prediction from model server", e);
        }
    }

    private void validateAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Audio file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file format. Only WAV and MP3 files are supported");
        }

        // You might want to add additional validation like file size limits
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
    }
} 