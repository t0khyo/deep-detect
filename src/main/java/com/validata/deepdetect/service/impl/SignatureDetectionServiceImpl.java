package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.exception.InvalidFileException;
import com.validata.deepdetect.exception.ModelServerException;
import com.validata.deepdetect.service.SignatureDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureDetectionServiceImpl implements SignatureDetectionService {
    @Value("${model.server.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREDICT_ENDPOINT = "/api/signature/predict";
    private static final String FIELD_GENUINE_SIGNATURE = "genuineSignature";
    private static final String FIELD_SIGNATURE_TO_VERIFY = "signatureToVerify";

    @Override
    public SignatureDetectionResponse verifySignature(MultipartFile genuineSignature, MultipartFile signatureToVerify) {
        validateFiles(genuineSignature, signatureToVerify);

        String url = baseUrl + PREDICT_ENDPOINT;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(FIELD_GENUINE_SIGNATURE, createFileResource(genuineSignature));
            body.add(FIELD_SIGNATURE_TO_VERIFY, createFileResource(signatureToVerify));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("Sending request to {} with files: {} and {}", url, 
                    genuineSignature.getOriginalFilename(), signatureToVerify.getOriginalFilename());

            ResponseEntity<SignatureDetectionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    SignatureDetectionResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ModelServerException("Model server returned unexpected response: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("Error during HTTP call to model server", e);
            throw new ModelServerException("Error during HTTP call to model server: " + e.getMessage(), e);
        }
    }

    private ByteArrayResource createFileResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new ModelServerException("Error reading file: " + file.getOriginalFilename(), e);
        }
    }

    private void validateFiles(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new InvalidFileException("File cannot be null or empty: " + file.getName());
            }
        }
    }
}


