package com.validata.deepdetect.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.validata.deepdetect.exception.JwtTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtConfigProperties jwtProperties;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(jwtProperties.expirationTime(), ChronoUnit.SECONDS);

        final String username = authentication.getName();
        final List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("roles", roles)
                .issuer(jwtProperties.issuer())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expirationTime))
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSSigner jwsSigner = new RSASSASigner(jwtProperties.privateKey());

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(),
                claims
        );

        try {
            signedJWT.sign(jwsSigner);
            log.info("Generated JWT for user: {}", username);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to sign JWT for user: {} - {}", username, e.getMessage());
            throw new JwtTokenException("Failed to sign JWT", e);
        }
    }

    public JWTClaimsSet validateToken(String token) {
        SignedJWT signedJWT;
        JWSVerifier verifier;
        JWTClaimsSet claims;

        try {
            signedJWT = SignedJWT.parse(token);
            verifier = new RSASSAVerifier(jwtProperties.publicKey());
            signedJWT.verify(verifier);
            claims = signedJWT.getJWTClaimsSet();

            if (this.tokenExpired(claims)) {
                log.error("Access token expired for user: {}", claims.getSubject());
                throw new JwtTokenException("Access token expired");
            }
            
            log.info("Successfully validated JWT for user: {}", claims.getSubject());
            return claims;
        } catch (ParseException e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw new JwtTokenException("Failed to parse token", e);
        } catch (JOSEException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtTokenException("Invalid JWT", e);
        }
    }

    public String extractUsername(JWTClaimsSet claims) {
        return claims.getSubject();
    }

    public List<String> extractRoles(JWTClaimsSet claims) {
        try {
            return claims.getStringListClaim("roles");
        } catch (ParseException e) {
            log.error("Failed to parse roles from JWT claims: {}", e.getMessage());
            throw new JwtTokenException("Failed to parse roles", e);
        }
    }

    public Date extractExpirationTime(JWTClaimsSet claims) {
        return claims.getExpirationTime();
    }

    public boolean tokenExpired(JWTClaimsSet claimsSet) {
        return claimsSet.getExpirationTime().before(new Date());
    }
}
