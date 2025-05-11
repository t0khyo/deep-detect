package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.dto.CustomerResponse;
import com.validata.deepdetect.exception.ResourceNotFoundException;
import com.validata.deepdetect.mapper.CustomerMapper;
import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.repository.CustomerRepository;
import com.validata.deepdetect.service.CustomerService;
import com.validata.deepdetect.service.SignatureStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final SignatureStorageService signatureStorageService;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating new customer with email: {}", request.email());
        Customer customer = customerMapper.toEntity(request);
        customer = customerRepository.save(customer);
        log.info("Successfully created customer with ID: {}", customer.getId());
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        log.info("Retrieving all customers");
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        log.info("Retrieving customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        log.info("Updating customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        customerMapper.updateEntityFromRequest(request, customer);
        customer = customerRepository.save(customer);
        log.info("Successfully updated customer with ID: {}", id);
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        if (customer.getSignatureUrl() != null) {
            log.info("Deleting signature for customer ID: {}", id);
            signatureStorageService.deleteSignature(customer.getSignatureUrl());
        }

        customerRepository.delete(customer);
        log.info("Successfully deleted customer with ID: {}", id);
    }

    @Override
    @Transactional
    public CustomerResponse uploadSignature(Long id, MultipartFile file) {
        log.info("Uploading signature for customer ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        if (customer.getSignatureUrl() != null) {
            log.info("Deleting existing signature for customer ID: {}", id);
            signatureStorageService.deleteSignature(customer.getSignatureUrl());
        }

        String signatureUrl = signatureStorageService.uploadSignature(file, id.toString());
        customer.setSignatureUrl(signatureUrl);
        customer = customerRepository.save(customer);
        log.info("Successfully uploaded signature for customer ID: {}", id);

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse deleteSignature(Long id) {
        log.info("Deleting signature for customer ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        if (customer.getSignatureUrl() != null) {
            signatureStorageService.deleteSignature(customer.getSignatureUrl());
            customer.setSignatureUrl(null);
            customer = customerRepository.save(customer);
            log.info("Successfully deleted signature for customer ID: {}", id);
        } else {
            log.info("No signature found for customer ID: {}", id);
        }

        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> searchCustomersByName(String query) {
        log.info("Searching customers with name containing: {}", query);
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }
}