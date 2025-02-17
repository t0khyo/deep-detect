package com.validata.deepdetect.service;

import com.validata.deepdetect.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Customer createCustomer(Customer customer);
    Optional<Customer> getCustomerById(Long id);
    List<Customer> searchCustomers( String searchQuery);
    boolean deleteCustomer(Long id);
}
