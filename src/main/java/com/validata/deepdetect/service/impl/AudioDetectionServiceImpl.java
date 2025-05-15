package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.AudioDetectionResponse;
import com.validata.deepdetect.service.AudioDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioDetectionServiceImpl implements AudioDetectionService {

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
            // TODO: Implement the actual audio prediction logic here
            // This is where you would integrate with your ML model
            // For now, returning a dummy response
            return new AudioDetectionResponse("Real audio");
        } catch (Exception e) {
            log.error("Error processing audio file", e);
            throw new RuntimeException("Failed to process audio file", e);
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