package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.SignUpRequest;
import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.exception.UserAlreadyExistsException;
import com.validata.deepdetect.mapper.UserMapper;
import com.validata.deepdetect.model.Role;
import com.validata.deepdetect.model.User;
import com.validata.deepdetect.repository.RoleRepository;
import com.validata.deepdetect.repository.UserRepository;
import com.validata.deepdetect.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse signup(SignUpRequest signUpRequest) {
        log.info("Attempting to sign up new user with email: {}", signUpRequest.email());
        if (userRepository.existsByEmail(signUpRequest.email())) {
            log.error("Signup failed - User already exists with email: {}", signUpRequest.email());
            throw new UserAlreadyExistsException("User with email " + signUpRequest.email() + " already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> {
                    log.error("User role not found in database");
                    return new RuntimeException("User role not found");
                });

        User user = User.builder()
                .firstName(signUpRequest.firstName())
                .lastName(signUpRequest.lastName())
                .email(signUpRequest.email())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .roles(List.of(userRole))
                .build();

        user = userRepository.save(user);
        log.info("Successfully created new user with ID: {}", user.getId());
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserProfile(String email) {
        log.info("Retrieving user profile for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
        return userMapper.toResponse(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by username/email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }
}