package com.validata.deepdetect.mapper;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.model.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest customerRequest) {
        if (customerRequest == null) {
            return null;
        }

        String signatureUrl = customerRequest.signatureUrl();
        if (signatureUrl != null && !signatureUrl.isEmpty()){
            signatureUrl = UUID.randomUUID().toString();
        }

        return Customer.builder()
                .firstName(customerRequest.firstName())
                .lastName(customerRequest.lastName())
                .signatureUrl(customerRequest.signatureUrl())
                .build();
    }
}