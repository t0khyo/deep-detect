package com.validata.deepdetect.exception;

import jakarta.persistence.EntityNotFoundException;

public class CustomerNotFoundException extends EntityNotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Customer with not found with id: " + id);
    }
}
