package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.service.StorageService;
import com.validata.deepdetect.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public String saveFile(MultipartFile file) {
        validateFile(file);
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                log.info("Creating upload directory at: {}", uploadPath);
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Failed to create upload directory at {}: {}", uploadPath, e.getMessage());
                throw new FileStorageException("Could not create upload directory: " + e.getMessage());
            }
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        try {
            log.info("Saving file {} to {}", file.getOriginalFilename(), filePath);
            file.transferTo(filePath);
            log.info("Successfully saved file {} to {}", file.getOriginalFilename(), filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to store file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ": " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            log.error("File name is null");
            throw new FileStorageException("File name cannot be null");
        }
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(dotIndex) : "";
    }

    @Override
    public byte[] loadFileAsBytes(String fileUrl) {
        try {
            log.info("Loading file from path: {}", fileUrl);
            Path path = Paths.get(fileUrl);
            if (!Files.exists(path)) {
                log.error("File not found at path: {}", fileUrl);
                throw new FileStorageException("File not found at path: " + fileUrl);
            }
            byte[] data = Files.readAllBytes(path);
            log.info("Successfully loaded file from path: {}", fileUrl);
            return data;
        } catch (IOException e) {
            log.error("Failed to read file from path {}: {}", fileUrl, e.getMessage());
            throw new FileStorageException("Could not read file from path " + fileUrl + ": " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null) {
            log.error("File is null");
            throw new FileStorageException("File cannot be null");
        }
        if (file.isEmpty()) {
            log.error("File is empty");
            throw new FileStorageException("File cannot be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("File size {} exceeds maximum limit of {} bytes", file.getSize(), MAX_FILE_SIZE);
            throw new FileStorageException("File size exceeds maximum limit of 10MB");
        }
    }
}





