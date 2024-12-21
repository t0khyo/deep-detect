package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.SignatureDetectionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SignatureDetectionService {
    public SignatureDetectionResponse verifySignature(MultipartFile genuineSignatureFile, MultipartFile signatureToVerifyFile);
}
