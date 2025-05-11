package com.validata.deepdetect.repository;

import com.validata.deepdetect.model.CustomerSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerSignatureRepository extends JpaRepository<CustomerSignature, Long> {
    Optional<CustomerSignature> findByCustomerId(Long customerId);
    void deleteByCustomerId(Long customerId);
} 