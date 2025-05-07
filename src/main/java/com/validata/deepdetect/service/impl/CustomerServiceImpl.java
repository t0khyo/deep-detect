package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.exception.CustomerNotFoundException;
import com.validata.deepdetect.mapper.CustomerMapper;
import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.repository.CustomerRepository;
import com.validata.deepdetect.service.CustomerService;
import com.validata.deepdetect.service.StorageService;

import com.validata.deepdetect.exception.FileStorageException;
import org.springframework.web.multipart.MultipartFile;
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
    private final StorageService storageService;

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

    @Override
    public Customer updateCustomerSignature(Long id, MultipartFile signatureFile) {
        log.info("Updating signature for customer with ID: {}", id);


        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Customer not found with ID: {}", id);
                    return new CustomerNotFoundException(id);
                });

        String fileUrl;
        try {
            fileUrl = storageService.saveFile(signatureFile);
        } catch (Exception e) {
            log.error("Error storing signature file for customer with ID: {}", id, e);
            throw new FileStorageException("Could not store file " + signatureFile.getOriginalFilename() + ". Please try again!");
        }


        customer.setSignatureUrl(fileUrl);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer's signature updated successfully with ID: {}", id);

        return updatedCustomer;
    }
    @Override
    public byte[] getCustomerSignature(Long id) {
        log.info("Fetching signature image for customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new CustomerNotFoundException(id);
                });

        if (customer.getSignatureUrl() == null || customer.getSignatureUrl().isEmpty()) {
            log.error("No signature found for customer ID: {}", id);
            throw new FileStorageException("No signature found for customer ID: " + id);
        }

        try {
            return storageService.loadFileAsBytes(customer.getSignatureUrl());
        } catch (Exception e) {
            log.error("Error loading signature image for customer ID: {}", id, e);
            throw new FileStorageException("Could not load signature image for customer ID: " + id);
        }
    }


}