package com.validata.deepdetect.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.SignatureVerificationResponse;
import com.validata.deepdetect.exception.CustomerNotFoundException;
import com.validata.deepdetect.exception.InvalidFileException;
import com.validata.deepdetect.exception.ModelServerException;
import com.validata.deepdetect.exception.StorageException;
import com.validata.deepdetect.repository.CustomerRepository;
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
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureDetectionServiceImpl implements SignatureDetectionService {
    private final CustomerRepository customerRepository;
    @Value("${model.server.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    private static final String PREDICT_ENDPOINT = "/api/signature/predict";
    private static final String FIELD_GENUINE_SIGNATURE = "genuineSignature";
    private static final String FIELD_SIGNATURE_TO_VERIFY = "signatureToVerify";

    @Override
    public SignatureVerificationResponse verifySignature(MultipartFile genuineSignature, MultipartFile signatureToVerify) {
        validateFile(genuineSignature);
        validateFile(signatureToVerify);

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

            ResponseEntity<SignatureVerificationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    SignatureVerificationResponse.class
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

    @Override
    public SignatureVerificationResponse verifyCustomerSignature(String customerId, MultipartFile signatureToVerify) {
        try {
            // Get the customer's signature URL
            String signatureUrl = getCustomerSignatureUrl(customerId);
            
            // Download the original signature
            ResponseEntity<byte[]> originalSignatureResponse = restTemplate.getForEntity(signatureUrl, byte[].class);
            if (originalSignatureResponse.getStatusCode() != HttpStatus.OK || originalSignatureResponse.getBody() == null) {
                throw new StorageException("Failed to download original signature from URL: " + signatureUrl);
            }

            // Create a ByteArrayResource from the downloaded signature
            ByteArrayResource genuineSignatureResource = new ByteArrayResource(originalSignatureResponse.getBody()) {
                @Override
                public String getFilename() {
                    return "genuine_signature.jpg";
                }
            };

            // Prepare the request to the model server
            String url = baseUrl + PREDICT_ENDPOINT;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(FIELD_GENUINE_SIGNATURE, genuineSignatureResource);
            body.add(FIELD_SIGNATURE_TO_VERIFY, createFileResource(signatureToVerify));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("Sending verification request to {} for customer {}", url, customerId);

            ResponseEntity<SignatureVerificationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    SignatureVerificationResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ModelServerException("Model server returned unexpected response: " + response.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("Error during HTTP call to model server for customer {}: {}", customerId, e.getMessage());
            throw new ModelServerException("Error during HTTP call to model server: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to verify signature for customer {}: {}", customerId, e.getMessage());
            throw new StorageException("Failed to verify signature", e);
        }
    }

    private String getCustomerSignatureUrl(String customerId) {
        return customerRepository.findById(Long.parseLong(customerId))
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId))
            .getSignatureUrl();
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

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be null or empty");
        }
    }
}


