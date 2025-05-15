package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.AudioDetectionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AudioDetectionService {
    AudioDetectionResponse predictAudio(MultipartFile audioFile);
} 