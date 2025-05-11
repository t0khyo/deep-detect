package com.validata.deepdetect.exception;

public class ApiValidationError extends ApiSubError {
    public ApiValidationError(String object, String message) {
        super(object, message);
    }

    public ApiValidationError(String object, String field, Object rejectedValue, String message) {
        super(object, field, rejectedValue, message);
    }
} 