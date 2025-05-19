package com.validata.deepdetect.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.validata.deepdetect.exception.FileStorageException;
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

    @Override
    public String uploadSignature(MultipartFile file, String customerId) {
        validateFile(file);
        
        String fileName = generateFileName(file, customerId);
        try {
            log.info("Uploading signature for customer {} to S3", customerId);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    file.getInputStream(),
                    metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putObjectRequest);
            String fileUrl = endpoint + "/" + bucketName + "/" + fileName;
            log.info("Successfully uploaded signature for customer {} to {}", customerId, fileUrl);
            return fileUrl;
        } catch (AmazonS3Exception e) {
            log.error("Failed to upload signature for customer {}: {}", customerId, e.getMessage());
            throw new FileStorageException("Failed to upload signature to S3: " + e.getMessage());
        } catch (IOException e) {
            log.error("Failed to read signature file for customer {}: {}", customerId, e.getMessage());
            throw new FileStorageException("Failed to read signature file: " + e.getMessage());
        }
    }

    @Override
    public void deleteSignature(String signatureUrl) {
        try {
            String key = extractKeyFromUrl(signatureUrl);
            log.info("Deleting signature from S3: {}", key);
            s3Client.deleteObject(bucketName, key);
            log.info("Successfully deleted signature from S3: {}", key);
        } catch (AmazonS3Exception e) {
            log.error("Failed to delete signature from S3: {}", e.getMessage());
            throw new FileStorageException("Failed to delete signature from S3: " + e.getMessage());
        }
    }

    @Override
    public String getSignatureUrl(String signatureKey) {
        try {
            log.info("Generating signed URL for signature: {}", signatureKey);
            if (!s3Client.doesObjectExist(bucketName, signatureKey)) {
                log.error("Signature not found in S3: {}", signatureKey);
                throw new FileStorageException("Signature not found: " + signatureKey);
            }
            String url = endpoint + "/" + bucketName + "/" + signatureKey;
            log.info("Successfully generated URL for signature: {}", url);
            return url;
        } catch (AmazonS3Exception e) {
            log.error("Failed to generate URL for signature {}: {}", signatureKey, e.getMessage());
            throw new FileStorageException("Failed to generate signature URL: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null) {
            log.error("Signature file is null");
            throw new FileStorageException("Signature file cannot be null");
        }
        if (file.isEmpty()) {
            log.error("Signature file is empty");
            throw new FileStorageException("Signature file cannot be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("Signature file size {} exceeds maximum limit of {} bytes", file.getSize(), MAX_FILE_SIZE);
            throw new FileStorageException("Signature file size exceeds maximum limit of 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.error("Invalid signature file type: {}", contentType);
            throw new FileStorageException("Invalid file type. Allowed types: JPEG, PNG, GIF");
        }
    }

    private String generateFileName(MultipartFile file, String customerId) {
        String extension = getFileExtension(file.getOriginalFilename());
        return "signatures/" + customerId + "/" + UUID.randomUUID().toString() + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            log.error("File name is null");
            throw new FileStorageException("File name cannot be null");
        }
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(dotIndex) : "";
    }

    private String extractKeyFromUrl(String url) {
        if (url == null || !url.startsWith(endpoint)) {
            log.error("Invalid signature URL: {}", url);
            throw new FileStorageException("Invalid signature URL format");
        }
        return url.substring(endpoint.length() + bucketName.length() + 2); // +2 for the slashes
    }
} 