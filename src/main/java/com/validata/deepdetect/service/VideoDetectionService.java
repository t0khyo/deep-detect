package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.VideoDetectionResponse;
import org.springframework.web.multipart.MultipartFile;
 
public interface VideoDetectionService {
    VideoDetectionResponse analyzeVideo(MultipartFile videoFile);
} 