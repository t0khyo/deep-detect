package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.model.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Customer createCustomer(CustomerRequest customerRequest);
    Customer getCustomerById(Long id);
    List<Customer> searchCustomers( String searchQuery);
    void deleteCustomer(Long id);

    List<Customer> getAllCustomers();

    Customer updateCustomerSignature(Long id, MultipartFile signatureFile);

    byte[] getCustomerSignature(Long customerId);
}
