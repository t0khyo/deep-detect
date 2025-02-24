package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }
}
