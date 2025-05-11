package com.validata.deepdetect.mapper;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.dto.CustomerResponse;
import com.validata.deepdetect.model.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .build();
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getSignatureUrl(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(CustomerRequest request, Customer customer) {
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());
    }
}