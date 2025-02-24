package com.validata.deepdetect.mapper;

import com.validata.deepdetect.dto.SignUpRequest;
import com.validata.deepdetect.dto.UserResponse;
import com.validata.deepdetect.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    User toEntity(SignUpRequest signUpRequest);

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    UserResponse toResponse(User user);
}
