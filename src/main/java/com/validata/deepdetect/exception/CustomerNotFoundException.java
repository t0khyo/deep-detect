package com.validata.deepdetect.exception;

import jakarta.persistence.EntityNotFoundException;

public class CustomerNotFoundException extends EntityNotFoundException {
    public CustomerNotFoundException(Long id) {
        super(String.format("Customer with id: {0} not found.", id));
    }
}
