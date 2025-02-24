package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.SignUpRequest;
import com.validata.deepdetect.dto.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserResponse signup(SignUpRequest signUpRequest);
    UserResponse getUserProfile(String email);
}
