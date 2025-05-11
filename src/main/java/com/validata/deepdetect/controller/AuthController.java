package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.LoginRequest;
import com.validata.deepdetect.dto.LoginResponse;
import com.validata.deepdetect.dto.SignUpRequest;
import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.service.UserService;
import com.validata.deepdetect.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Missing Required Fields",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "Validation failed",
                                "errors": [
                                    {
                                        "field": "email",
                                        "message": "Email is required"
                                    },
                                    {
                                        "field": "password",
                                        "message": "Password must be at least 8 characters"
                                    }
                                ]
                            }"""
                    ),
                    @ExampleObject(
                        name = "Invalid Email Format",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "Invalid email format",
                                "path": "/api/v1/auth/signup"
                            }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Email Already Exists",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 409,
                                "message": "Email already registered",
                                "path": "/api/v1/auth/signup"
                            }"""
                    )
                }
            )
        )
    })
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Invalid Credentials",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 401,
                                "message": "Invalid username or password",
                                "path": "/api/v1/auth/login"
                            }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Missing Required Fields",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "Username and password are required",
                                "path": "/api/v1/auth/login"
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
