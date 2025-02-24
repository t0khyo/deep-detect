package com.validata.deepdetect.mapper;

import com.validata.deepdetect.dto.SignUpRequest;
import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.model.User;
import org.mapstruct.Mapper;

// todo: not working properly fix it
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignUpRequest signUpRequest);
    UserResponse toResponse(User user);
}

