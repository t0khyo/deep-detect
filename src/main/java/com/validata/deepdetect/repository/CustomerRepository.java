package com.validata.deepdetect.repository;

import com.validata.deepdetect.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByFirstNameContainingOrLastNameContainingOrId(String firstName, String lastName, Long id);
}
