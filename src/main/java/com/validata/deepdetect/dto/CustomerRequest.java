package com.validata.deepdetect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(
    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @Email(message = "Invalid email format")
    String email,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    String address
) {}
