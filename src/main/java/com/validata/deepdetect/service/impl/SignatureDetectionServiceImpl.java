package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.exception.InvalidFileException;
import com.validata.deepdetect.exception.ModelServerException;
import com.validata.deepdetect.service.SignatureDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureDetectionServiceImpl implements SignatureDetectionService {
    @Value("${model.server.base-url}")
    private String baseUrl;

    private static final String PREDICT_ENDPOINT = "/predict";
    private static final String FIELD_GENUINE_SIGNATURE = "genuineSignature";
    private static final String FIELD_SIGNATURE_TO_VERIFY = "signatureToVerify";

    private final RestTemplate restTemplate = new RestTemplate();

    public SignatureDetectionResponse verifySignature(MultipartFile genuineSignature, MultipartFile signatureToVerify) {
        validateFiles(genuineSignature, signatureToVerify);

        String url = baseUrl + PREDICT_ENDPOINT;

        try {
            // Prepare the files for the POST request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(FIELD_GENUINE_SIGNATURE, genuineSignature.getResource());
            body.add(FIELD_SIGNATURE_TO_VERIFY, signatureToVerify.getResource());

            // Create the request entity with the files
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send the POST request
            log.info("Sending request to: {}", url);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return parseResponse(responseEntity.getBody());
            } else {
                throw new ModelServerException("Model server error. HTTP code: " + responseEntity.getStatusCodeValue());
            }
        } catch (HttpStatusCodeException e) {
            throw new ModelServerException("Error during HTTP call to model server: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ModelServerException("Error processing response", e);
        }
    }

    private void validateFiles(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new InvalidFileException("File cannot be null or empty: " + file.getName());
            }
        }
    }

    private SignatureDetectionResponse parseResponse(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, SignatureDetectionResponse.class);
    }
}
