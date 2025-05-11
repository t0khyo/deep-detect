package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.dto.CustomerResponse;
import com.validata.deepdetect.service.CustomerService;
import com.validata.deepdetect.service.SignatureStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;
    private final SignatureStorageService signatureStorageService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/signature", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload customer signature")
    public ResponseEntity<CustomerResponse> uploadSignature(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(customerService.uploadSignature(id, file));
    }

    @DeleteMapping("/{id}/signature")
    @Operation(summary = "Delete customer signature")
    public ResponseEntity<CustomerResponse> deleteSignature(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.deleteSignature(id));
    }
}
