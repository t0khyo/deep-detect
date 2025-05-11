package com.validata.deepdetect.service;

import com.validata.deepdetect.model.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Customer getCustomerById(Long id);
    List<Customer> getAllCustomers();
    List<Customer> searchCustomers(String searchQuery);
    Customer updateCustomer(Long id, Customer customerDetails);
    void deleteCustomer(Long id);
    void uploadSignature(Long customerId, MultipartFile file);
    byte[] getCustomerSignature(Long customerId);
    List<Customer> searchByName(String query);
}
