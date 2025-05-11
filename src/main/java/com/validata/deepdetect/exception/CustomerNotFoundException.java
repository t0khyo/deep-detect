package com.validata.deepdetect.exception;

import jakarta.persistence.EntityNotFoundException;

public class CustomerNotFoundException extends EntityNotFoundException {
    public CustomerNotFoundException(Long id) {
        super(String.format("Customer not found with id: %d", id));
    }

    public CustomerNotFoundException(String identifier) {
        super(String.format("Customer not found with identifier: %s", identifier));
    }

    public CustomerNotFoundException(String field, String value) {
        super(String.format("Customer not found with %s: %s", field, value));
    }
}
