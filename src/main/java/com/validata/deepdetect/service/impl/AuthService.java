package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.LoginRequest;
import com.validata.deepdetect.dto.LoginResponse;
import com.validata.deepdetect.exception.InvalidEmailOrPassword;
import com.validata.deepdetect.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            String token = jwtUtil.generateToken(authentication);

//            log.info("User logged in: {}", authentication.getPrincipal());
            return new LoginResponse(token);
        } catch (BadCredentialsException e) {
            log.error("login(): BadCredentialsException");
            throw new InvalidEmailOrPassword();
        }
    }
}
