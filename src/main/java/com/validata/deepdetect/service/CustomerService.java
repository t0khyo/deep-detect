package com.validata.deepdetect.service;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.dto.CustomerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(Long id);
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    void deleteCustomer(Long id);
    CustomerResponse uploadSignature(Long id, MultipartFile file);
    CustomerResponse deleteSignature(Long id);
    List<CustomerResponse> searchCustomersByName(String query);
}
