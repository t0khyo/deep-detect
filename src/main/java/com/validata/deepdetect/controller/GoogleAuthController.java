package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.GoogleTokenRequest;
import com.validata.deepdetect.dto.JwtResponse;
import com.validata.deepdetect.service.GoogleAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
@Tag(name = "Social Authentication", description = "APIs for social login like Google")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    @Operation(summary = "Login with Google", description = "Login a user using Google ID token and return JWT")
    @ApiResponse(responseCode = "200", description = "Google login successful")
    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody GoogleTokenRequest request) {
        JwtResponse jwt = googleAuthService.loginWithGoogle(request.getIdToken());
        return ResponseEntity.ok(jwt);
    }
}

