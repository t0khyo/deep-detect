package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    public ResponseEntity<UserResponse> getUserProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }
}
