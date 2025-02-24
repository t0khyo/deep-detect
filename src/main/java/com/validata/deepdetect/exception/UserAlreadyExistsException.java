package com.validata.deepdetect.exception;

import jakarta.validation.constraints.Email;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
