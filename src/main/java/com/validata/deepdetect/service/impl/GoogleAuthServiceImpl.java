package com.validata.deepdetect.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.validata.deepdetect.dto.GoogleUserInfo;
import com.validata.deepdetect.dto.JwtResponse;
import com.validata.deepdetect.mapper.GoogleUserMapper;
import com.validata.deepdetect.model.AuthProvider;
import com.validata.deepdetect.model.Role;
import com.validata.deepdetect.model.User;
import com.validata.deepdetect.repository.RoleRepository;
import com.validata.deepdetect.repository.UserRepository;
import com.validata.deepdetect.service.GoogleAuthService;
import com.validata.deepdetect.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GoogleUserMapper googleUserMapper;
    private final JwtService jwtService;

    @Override
    public JwtResponse loginWithGoogle(String idToken) {
        // 1. Verify token with Google
        String googleApiUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        JsonNode userInfo;
        try {
            userInfo = restTemplate.getForObject(googleApiUrl, JsonNode.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Google token");
        }

        // 2. Extract user info
        String email = userInfo.get("email").asText();
        String name = userInfo.get("name").asText();
        String picture = userInfo.has("picture") ? userInfo.get("picture").asText() : "";

        // 3. Check if user exists
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            // 4. Create new user
            GoogleUserInfo googleUser = new GoogleUserInfo();
            googleUser.setEmail(email);
            googleUser.setName(name);
            googleUser.setPicture(picture);

            user = googleUserMapper.toUser(googleUser);
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setPassword(""); // not needed
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            user.setRoles(Collections.singletonList(userRole));

            user = userRepository.save(user);
        }

        // 5. Generate JWT
        String jwt = jwtService.generateToken(user);

        return new JwtResponse(jwt);
    }
}

