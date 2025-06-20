package com.validata.deepdetect.service;

import com.validata.deepdetect.model.User;

public interface JwtService {
    String generateToken(User user);
}
