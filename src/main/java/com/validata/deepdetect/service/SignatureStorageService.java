package com.validata.deepdetect.service;

import org.springframework.web.multipart.MultipartFile;

public interface SignatureStorageService {
    String uploadSignature(MultipartFile file, String customerId);
    void deleteSignature(String signatureUrl);
    String getSignatureUrl(String signatureKey);
} 