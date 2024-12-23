package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.service.SignatureDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class SignatureDetectionServiceImpl implements SignatureDetectionService {
    private final String AI_MODEL_SIGNATURE_ENDPOINT = "https://localhost:8081/signature/predict";
    private final HttpClient client = HttpClient.newHttpClient();
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
        try {
            // Build the multipart body
            String boundary = "Boundary-" + System.currentTimeMillis();
            String body = buildMultipartBody(boundary, genuineSignatureFile, signatureToVerifyFile);

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AI_MODEL_SIGNATURE_ENDPOINT))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            // Send the request and parse the response
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Map JSON response to SignatureDetectionResponse
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), SignatureDetectionResponse.class);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        }
    }

    private String buildMultipartBody(String boundary, MultipartFile genuineSignature, MultipartFile signatureToVerify) throws IOException {
        StringBuilder body = new StringBuilder();

        // Add the genuine signature
        body.append("--").append(boundary).append("\r\n");
        body.append("Content-Disposition: form-data; name=\"genuineSignature\"; filename=\"")
                .append(genuineSignature.getOriginalFilename()).append("\"\r\n");
        body.append("Content-Type: ").append(genuineSignature.getContentType()).append("\r\n\r\n");
        body.append(new String(genuineSignature.getBytes())).append("\r\n");

        // Add the signature to verify
        body.append("--").append(boundary).append("\r\n");
        body.append("Content-Disposition: form-data; name=\"signatureToVerify\"; filename=\"")
                .append(signatureToVerify.getOriginalFilename()).append("\"\r\n");
        body.append("Content-Type: ").append(signatureToVerify.getContentType()).append("\r\n\r\n");
        body.append(new String(signatureToVerify.getBytes())).append("\r\n");

        // End the boundary
        body.append("--").append(boundary).append("--");

        return body.toString();
    }
}

