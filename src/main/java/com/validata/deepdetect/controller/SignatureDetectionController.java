package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.service.SignatureDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/books")
@RestController
public class SignatureDetectionController {
    private SignatureDetectionService detectionService;

    public ResponseEntity<SignatureDetectionResponse> verifySignature(
            MultipartFile genuineSignature,
            MultipartFile signatureToVerify
    ) {
        return ResponseEntity.ok(detectionService.verifySignature(genuineSignature, signatureToVerify));
    }
}
