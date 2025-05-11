package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user profiles and information")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(
        summary = "Get user profile",
        description = "Retrieves the profile information of the currently authenticated user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Unauthorized",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 401,
                                "message": "Full authentication is required to access this resource",
                                "path": "/api/v1/users/profile"
                            }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User profile not found",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Profile Not Found",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 404,
                                "message": "User profile not found",
                                "path": "/api/v1/users/profile"
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<UserResponse> getUserProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }
}
