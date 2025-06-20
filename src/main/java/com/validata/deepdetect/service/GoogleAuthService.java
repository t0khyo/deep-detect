package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.JwtResponse;

public interface GoogleAuthService {
    JwtResponse loginWithGoogle(String idToken);
}
