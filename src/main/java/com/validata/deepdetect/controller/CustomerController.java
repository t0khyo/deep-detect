package com.validata.deepdetect.controller;

import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Customer Management", description = "APIs for managing customer information and signatures")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves customer information by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    @ApiResponse(responseCode = "200", description = "List of customers retrieved successfully")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers by name", description = "Searches for customers by their first or last name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search query")
    })
    public ResponseEntity<List<Customer>> searchCustomers(
        @Parameter(description = "Search query for customer name", example = "John")
        @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(customerService.searchByName(query));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Updates an existing customer's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Customer> updateCustomer(
        @PathVariable Long id,
        @RequestBody Customer customerDetails
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Deletes a customer and their associated data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/signature", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload customer signature", description = "Uploads a signature image for a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signature uploaded successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
        @ApiResponse(responseCode = "500", description = "Error processing signature")
    })
    public ResponseEntity<Void> uploadSignature(
        @PathVariable Long id,
        @Parameter(description = "Signature image file (JPEG, PNG, or GIF, max 5MB)")
        @RequestParam("file") MultipartFile file
    ) {
        customerService.uploadSignature(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/signature")
    @Operation(summary = "Get customer signature", description = "Retrieves a customer's signature image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signature retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Customer or signature not found")
    })
    public ResponseEntity<byte[]> getCustomerSignature(@PathVariable Long id) {
        byte[] signature = customerService.getCustomerSignature(id);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(signature);
    }
}
