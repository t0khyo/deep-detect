package com.validata.deepdetect.controller;

import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path= "api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

@PostMapping
public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
    Customer createdCustomer = customerService.createCustomer(customer);
    return ResponseEntity.ok(createdCustomer);
}

@GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id){
    Optional<Customer> customer= customerService.getCustomerById(id);
    if (customer.isPresent()){
        return ResponseEntity.ok(customer.get());
    }else {
        return ResponseEntity.notFound().build();
    }
}

    @GetMapping
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam(required = false) String searchQuery) {
        List<Customer> customers = customerService.searchCustomers(searchQuery);
        if (customers.isEmpty()){
            return ResponseEntity.ok(Collections.emptyList());
        }else {
            return ResponseEntity.ok(customers);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return ResponseEntity.ok("Customer with ID " + id + " has been deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Customer with ID " + id + " not found.");
        }
    }

}
