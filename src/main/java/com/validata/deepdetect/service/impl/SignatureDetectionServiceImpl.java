package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.SignatureDetectionResponse;
import com.validata.deepdetect.exception.InvalidFileException;
import com.validata.deepdetect.exception.ModelServerException;
import com.validata.deepdetect.service.SignatureDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureDetectionServiceImpl implements SignatureDetectionService {
    @Value("${model.server.base-url}")
    private String baseUrl;

    private static final String PREDICT_ENDPOINT = "/signature/predict";
    private static final String FIELD_GENUINE_SIGNATURE = "genuineSignature";
    private static final String FIELD_SIGNATURE_TO_VERIFY = "signatureToVerify";

    public SignatureDetectionResponse verifySignature(MultipartFile genuineSignature, MultipartFile signatureToVerify) {
        validateFiles(genuineSignature, signatureToVerify);

        String url = baseUrl + PREDICT_ENDPOINT;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody(FIELD_GENUINE_SIGNATURE, genuineSignature.getInputStream(),
                    getContentType(genuineSignature), genuineSignature.getOriginalFilename());
            builder.addBinaryBody(FIELD_SIGNATURE_TO_VERIFY, signatureToVerify.getInputStream(),
                    getContentType(signatureToVerify), signatureToVerify.getOriginalFilename());

            httpPost.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                log.info("Request to {} with files: {} and {}", url, genuineSignature.getOriginalFilename(),
                        signatureToVerify.getOriginalFilename());
                if (response.getCode() == 200) {
                    return parseResponse(response.getEntity());
                } else {
                    throw new ModelServerException("Model server error. HTTP code: " + response.getCode()) ;
                }
            }
        } catch (IOException e) {
            throw new ModelServerException("Error during HTTP call to model server", e);
        }
    }

    private ContentType getContentType(MultipartFile file) {
        try {
            return ContentType.parse(file.getContentType());
        } catch (Exception e) {
            log.warn("Invalid content type for file {}. Defaulting to application/octet-stream.", file.getOriginalFilename());
            return ContentType.APPLICATION_OCTET_STREAM;
        }
    }

    private void validateFiles(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new InvalidFileException("File cannot be null or empty: " + file.getName());
            }
        }
    }


    private SignatureDetectionResponse parseResponse(HttpEntity entity) throws IOException {
        String jsonResponse = new String(entity.getContent().readAllBytes());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, SignatureDetectionResponse.class);
    }
}


