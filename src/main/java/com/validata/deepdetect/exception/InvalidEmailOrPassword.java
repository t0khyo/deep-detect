package com.validata.deepdetect.exception;

public class InvalidEmailOrPassword extends RuntimeException{
    public InvalidEmailOrPassword() {
        super("Invalid Email or Password");
    }
}
