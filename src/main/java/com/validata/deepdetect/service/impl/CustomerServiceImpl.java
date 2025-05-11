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
        Customer customer = customerMapper.toEntity(request);
        customer = customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customerMapper.updateEntityFromRequest(request, customer);
        customer = customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (customer.getSignatureUrl() != null) {
            signatureStorageService.deleteSignature(customer.getSignatureUrl());
        }

        customerRepository.delete(customer);
    }

    @Override
    @Transactional
    public CustomerResponse uploadSignature(Long id, MultipartFile file) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (customer.getSignatureUrl() != null) {
            signatureStorageService.deleteSignature(customer.getSignatureUrl());
        }

        String signatureUrl = signatureStorageService.uploadSignature(file, id.toString());
        customer.setSignatureUrl(signatureUrl);
        customer = customerRepository.save(customer);

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse deleteSignature(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (customer.getSignatureUrl() != null) {
            signatureStorageService.deleteSignature(customer.getSignatureUrl());
            customer.setSignatureUrl(null);
            customer = customerRepository.save(customer);
        }

        return customerMapper.toResponse(customer);
    }
}