package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.service.StorageService;
import com.validata.deepdetect.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file) {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new FileStorageException("Could not create upload directory.");
            }
        }


        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + fileExtension;


        Path filePath = uploadPath.resolve(fileName);

        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
        }

        return filePath.toString();
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(dotIndex) : "";
    }


    @Override
    public byte[] loadFileAsBytes(String fileUrl) {
        try {
            Path path = Paths.get(fileUrl);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new FileStorageException("Could not read file from path: " + fileUrl);
        }
    }
}





