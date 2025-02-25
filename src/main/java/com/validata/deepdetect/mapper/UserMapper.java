package com.validata.deepdetect.mapper;

import com.validata.deepdetect.dto.SignUpRequest;
import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static User toEntity(SignUpRequest signUpRequest) {
        return User.builder()
                .firstName(signUpRequest.firstName())
                .lastName(signUpRequest.lastName())
                .email(signUpRequest.email())
                .password(signUpRequest.password())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
