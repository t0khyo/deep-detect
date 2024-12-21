package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.service.SignatureDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SignatureDetectionServiceImpl implements SignatureDetectionService {
    private final String AI_MODEL_SIGNATURE_ENDPOINT = "https://localhost:8081/signature/predict";
    /* todo:
        this method should call tha ai model api to predict forgery attempts.

        AI_MODEL_SIGNATURE_ENDPOINT accepts these inputs:
            1. genuineSignature (image file)
            2. signatureToVerify (image file)

        response should have:
            1. similarityPercentage (float)
            2. probabilityPercentage (float)
            3. signatureWasNotForged (boolean)
     */
    @Override
    public SignatureDetectionResponse verifySignature(MultipartFile genuineSignatureFile, MultipartFile signatureToVerifyFile) {

        return null;
    }
}
