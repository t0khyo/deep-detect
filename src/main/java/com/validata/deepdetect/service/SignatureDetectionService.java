package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.SignatureVerificationResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SignatureDetectionService {
    SignatureVerificationResponse verifySignature(MultipartFile genuineSignature, MultipartFile signatureToVerify);
    SignatureVerificationResponse verifyCustomerSignature(String customerId, MultipartFile signatureToVerify);
}
