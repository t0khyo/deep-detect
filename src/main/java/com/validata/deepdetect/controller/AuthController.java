package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.LoginRequest;
import com.validata.deepdetect.dto.LoginResponse;
import com.validata.deepdetect.dto.SignUpRequest;
import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.service.UserService;
import com.validata.deepdetect.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided details"
    )
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    public ResponseEntity<UserResponse> signUp(
            @RequestBody SignUpRequest signUpRequest
    ) {
        return ResponseEntity.ok(userService.signup(signUpRequest));
    }

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponse(responseCode = "200", description = "Login successful")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


//    @PostMapping("/google")
//    @Operation(summary = "Google login", description = "Authenticates a user using Google token and returns a JWT")
//    @ApiResponse(responseCode = "200", description = "Google login successful")
//    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody GoogleTokenRequest request) {
//        return ResponseEntity.ok(googleAuthService.loginWithGoogle(request.getIdToken()));
//    }
}
