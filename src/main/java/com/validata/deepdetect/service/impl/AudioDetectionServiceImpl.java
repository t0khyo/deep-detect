package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.AudioDetectionResponse;
import com.validata.deepdetect.exception.ModelServerException;
import com.validata.deepdetect.service.AudioDetectionService;
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
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioDetectionServiceImpl implements AudioDetectionService {
    private static final String PREDICT_ENDPOINT = "/api/audio/predict";
    private static final String FIELD_AUDIO = "audio";
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "audio/mpeg",     // .mp3
            "audio/wav",      // .wav
            "audio/x-wav",    // .wav (some browsers)
            "audio/x-m4a",    // .m4a (Apple format)
            "audio/aac",      // .aac
            "audio/ogg",      // .ogg
            "audio/webm",      // .webm audio
            "audio/mp3"
    );
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${model.server.base-url}")
    private String baseUrl;

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
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (rawResponse.getStatusCode() == HttpStatus.OK && rawResponse.getBody() != null) {
                String responseBody = rawResponse.getBody();

                // Parse the response as JsonNode to inspect the structure
                JsonNode rootNode = objectMapper.readTree(responseBody);

                // Check if the response contains error information
                if (rootNode.has("error")) {
                    String errorMessage = rootNode.get("error").asText();
                    log.error("Model server returned error: {}", errorMessage);
                    throw new ModelServerException("Model server error: " + errorMessage);
                }

                // Convert to AudioDetectionResponse
                AudioDetectionResponse response = objectMapper.readValue(responseBody, AudioDetectionResponse.class);
                log.info("Successfully analyzed audio: {} - Prediction: {}",
                        audioFile.getOriginalFilename(),
                        response.getPrediction());
                return response;
            } else {
                String errorMessage = "Model server returned unexpected response: " + rawResponse.getStatusCode();
                log.error(errorMessage);
                throw new ModelServerException(errorMessage);
            }
        } catch (RestClientException e) {
            log.error("Error during audio analysis request: {}", e.getMessage());
            throw new ModelServerException("Error during audio analysis: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error reading audio file: {}", e.getMessage());
            throw new ModelServerException("Error reading audio file: " + audioFile.getOriginalFilename(), e);
        } catch (Exception e) {
            log.error("Error parsing model server response: {}", e.getMessage());
            throw new ModelServerException("Error parsing model server response: " + e.getMessage(), e);
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

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
    }
} 