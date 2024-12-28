package com.validata.deepdetect.exception;

public class ModelServerException extends RuntimeException {
    public ModelServerException(String message) {
        super(message);
    }

    public ModelServerException(String message, Throwable cause) {
        super(message, cause);
    }
}

