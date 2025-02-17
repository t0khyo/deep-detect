package com.validata.deepdetect.service.impl;

import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.repository.CustomerRepository;
import com.validata.deepdetect.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer (Customer customer){
       return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> getCustomerById(Long id){
        return customerRepository.findById(id);
    }

    @Override
    public List<Customer> searchCustomers(String searchQuery){
        Long id = tryParseLong(searchQuery);
        return customerRepository.findByFirstNameContainingOrLastNameContainingOrId(searchQuery,searchQuery,id);
    }
//    @Override
//    public void deleteCustomer( Long id){
//        customerRepository.deleteById(id);
//    }
public boolean deleteCustomer(Long id) {
    Optional<Customer> customer = customerRepository.findById(id);
    if (customer.isPresent()) {
        customerRepository.deleteById(id);
        return true;
    } else {
        return false;
    }
}

    private Long tryParseLong(String searchQuery) {
        try {
            return Long.parseLong(searchQuery);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
