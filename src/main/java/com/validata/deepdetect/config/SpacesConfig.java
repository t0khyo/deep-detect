package com.validata.deepdetect.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class SpacesConfig {

    @Value("${spaces.endpoint}")
    private String endpoint;

    @Value("${spaces.access-key}")
    private String accessKey;

    @Value("${spaces.secret-key}")
    private String secretKey;

    @Value("${spaces.region}")
    private String region;

    @Value("${spaces.bucket-name}")
    private String bucketName;

    @Bean
    public AmazonS3 spacesClient() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .build();

        // Create bucket if it doesn't exist
        if (!s3Client.doesBucketExistV2(bucketName)) {
            log.info("Creating bucket: {}", bucketName);
            Bucket bucket = s3Client.createBucket(bucketName);
            log.info("Bucket created: {}", bucket.getName());

            // Set bucket policy to make it public
            String bucketPolicy = String.format(
                "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"PublicReadGetObject\",\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":[\"s3:GetObject\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::%s\",\"arn:aws:s3:::%s/*\"]}]}",
                bucketName, bucketName
            );
            s3Client.setBucketPolicy(bucketName, bucketPolicy);

            // Set CORS configuration
            CORSRule rule = new CORSRule()
                .withAllowedMethods(Arrays.asList(
                    CORSRule.AllowedMethods.GET,
                    CORSRule.AllowedMethods.PUT,
                    CORSRule.AllowedMethods.POST,
                    CORSRule.AllowedMethods.DELETE,
                    CORSRule.AllowedMethods.HEAD
                ))
                .withAllowedOrigins(Arrays.asList("*"))
                .withAllowedHeaders(Arrays.asList("*"))
                .withMaxAgeSeconds(3000);

            BucketCrossOriginConfiguration corsConfig = new BucketCrossOriginConfiguration(Arrays.asList(rule));
            s3Client.setBucketCrossOriginConfiguration(bucketName, corsConfig);

            // Set bucket ACL to public-read
            s3Client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }

        return s3Client;
    }
} 