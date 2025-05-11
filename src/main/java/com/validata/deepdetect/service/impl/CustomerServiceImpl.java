package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.exception.CustomerNotFoundException;
import com.validata.deepdetect.exception.InvalidFileException;
import com.validata.deepdetect.mapper.CustomerMapper;
import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.model.CustomerSignature;
import com.validata.deepdetect.repository.CustomerRepository;
import com.validata.deepdetect.repository.CustomerSignatureRepository;
import com.validata.deepdetect.service.CustomerService;
import com.validata.deepdetect.service.StorageService;

import com.validata.deepdetect.exception.FileStorageException;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerSignatureRepository signatureRepository;
    private final CustomerMapper customerMapper;
    private final StorageService storageService;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
        "image/jpeg",
        "image/png",
        "image/gif"
    );

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("Creating a new customer: {}", customer.getEmail());
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(Long id) {
        log.info("Fetching customer by ID: {}", id);
        return customerRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Customer not found with ID: {}", id);
                return new CustomerNotFoundException(id);
            });
    }

    @Override
    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomer(Long id, Customer customerDetails) {
        log.info("Updating customer with id: {}", id);
        Customer customer = getCustomerById(id);
        
        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setAddress(customerDetails.getAddress());
        
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
        log.info("Customer deleted successfully with ID: {}", id);
    }

    @Override
    public List<Customer> searchCustomers(String searchQuery) {
        log.info("Searching customers with query: {}", searchQuery);
        List<Customer> customers = customerRepository.searchByQuery(searchQuery);
        log.info("Found {} customers matching the query: {}", customers.size(), searchQuery);
        return customers;
    }

    @Override
    @Transactional
    public void uploadSignature(Long customerId, MultipartFile file) {
        log.info("Uploading signature for customer: {}", customerId);
        Customer customer = getCustomerById(customerId);
        
        validateFile(file);
        
        try {
            CustomerSignature signature = CustomerSignature.builder()
                .customer(customer)
                .signatureData(file.getBytes())
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();
            
            // Delete existing signature if any
            signatureRepository.deleteByCustomerId(customerId);
            
            // Save new signature
            signatureRepository.save(signature);
            log.info("Signature uploaded successfully for customer: {}", customerId);
        } catch (IOException e) {
            log.error("Error uploading signature for customer: {}", customerId, e);
            throw new InvalidFileException("Failed to process signature file");
        }
    }

    @Override
    public byte[] getCustomerSignature(Long customerId) {
        log.info("Fetching signature for customer: {}", customerId);
        CustomerSignature signature = signatureRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Signature not found for customer: " + customerId));
        return signature.getSignatureData();
    }

    @Override
    public List<Customer> searchByName(String query) {
        log.info("Searching customers by name with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            log.info("Empty search query, returning all customers");
            return getAllCustomers();
        }
        List<Customer> results = customerRepository.searchByName(query.trim());
        log.info("Found {} customers matching the name query: {}", results.size(), query);
        return results;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Signature file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("Signature file size exceeds maximum limit of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileException("Invalid file type. Allowed types: JPEG, PNG, GIF");
        }
    }
}