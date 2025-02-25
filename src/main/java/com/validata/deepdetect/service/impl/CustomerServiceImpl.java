package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.exception.CustomerNotFoundException;
import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.repository.CustomerRepository;
import com.validata.deepdetect.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException(id));
    }

    @Override
    public List<Customer> searchCustomers(String searchQuery) {
        return customerRepository.searchByQuery(searchQuery);
    }

    public void deleteCustomer(Long id) {
        Customer customer = this.getCustomerById(id);
        customerRepository.delete(customer);
    }

}
