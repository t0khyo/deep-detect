package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.exception.CustomerNotFoundException;
import com.validata.deepdetect.mapper.CustomerMapper;
import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.repository.CustomerRepository;
import com.validata.deepdetect.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Customer createCustomer(CustomerRequest customerRequest) {
        log.info("Creating a new customer with request: {}", customerRequest);
        Customer customer = customerMapper.toEntity(customerRequest);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    @Override
    public Customer getCustomerById(Long id) {
        log.info("Fetching customer by ID: {}", id);
        return customerRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Customer not found with ID: {}", id);
                    return new CustomerNotFoundException(id);
                });
    }

    @Override
    public List<Customer> searchCustomers(String searchQuery) {
        log.info("Searching customers with query: {}", searchQuery);
        List<Customer> customers = customerRepository.searchByQuery(searchQuery);
        log.info("Found {} customers matching the query: {}", customers.size(), searchQuery);
        return customers;
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        Customer customer = this.getCustomerById(id);
        customerRepository.delete(customer);
        log.info("Customer deleted successfully with ID: {}", id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}