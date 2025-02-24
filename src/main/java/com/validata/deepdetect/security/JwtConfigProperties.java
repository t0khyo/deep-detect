package com.validata.deepdetect.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "jwt.config")
public record JwtConfigProperties(
        Long expirationTime,
        String issuer,
        RSAPrivateKey privateKey,
        RSAPublicKey publicKey
) {
}
