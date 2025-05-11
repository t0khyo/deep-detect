package com.validata.deepdetect.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.validata.deepdetect.exception.StorageException;
import com.validata.deepdetect.service.SignatureStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpacesSignatureStorageService implements SignatureStorageService {

    private final AmazonS3 s3Client;

    @Value("${spaces.bucket-name}")
    private String bucketName;

    @Value("${spaces.endpoint}")
    private String endpoint;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new StorageException("File size exceeds maximum limit of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new StorageException("File type not allowed. Allowed types: JPEG, PNG, GIF");
        }
    }

    @Override
    public String uploadSignature(MultipartFile file, String customerId) {
        try {
            validateFile(file);

            String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            String key = String.format("signatures/%s/%s", customerId, fileName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putObjectRequest);

            return s3Client.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new StorageException("Failed to store file", e);
        } catch (Exception e) {
            log.error("Failed to store file", e);
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public void deleteSignature(String signatureUrl) {
        try {
            String key = extractKeyFromUrl(signatureUrl);
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Failed to delete file", e);
            throw new StorageException("Failed to delete file", e);
        }
    }

    private String extractKeyFromUrl(String url) {
        // Remove the bucket name and domain from the URL
        String key = url.substring(url.indexOf(bucketName) + bucketName.length() + 1);
        // Remove any query parameters
        int queryIndex = key.indexOf('?');
        if (queryIndex > 0) {
            key = key.substring(0, queryIndex);
        }
        return key;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    @Override
    public String getSignatureUrl(String signatureKey) {
        return String.format("https://%s.%s/%s", bucketName, endpoint.replace("https://", ""), signatureKey);
    }
} 