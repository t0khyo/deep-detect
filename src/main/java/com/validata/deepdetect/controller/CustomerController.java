package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.dto.CustomerSignatureResponse;
import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/customers")
@Tag(name = "Customer Management", description = "APIs for managing customer information and signatures")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @Operation(
        summary = "Create a new customer",
        description = "Creates a new customer record with the provided details"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = Customer.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Missing Required Fields",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "Validation failed",
                                "errors": [
                                    {
                                        "field": "firstName",
                                        "message": "First name is required"
                                    },
                                    {
                                        "field": "email",
                                        "message": "Valid email is required"
                                    }
                                ]
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(customerRequest));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get customer by ID",
        description = "Retrieves customer information by their unique identifier"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Customer found",
            content = @Content(schema = @Schema(implementation = Customer.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Customer Not Found",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 404,
                                "message": "Customer with ID 123 not found",
                                "path": "/api/v1/customers/123"
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    @Operation(
        summary = "Get all customers",
        description = "Retrieves a list of all customers in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of customers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Customer.class))
        )
    })
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search customers",
        description = "Searches customers based on the provided query string"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content = @Content(schema = @Schema(implementation = Customer.class))
        )
    })
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(customerService.searchCustomers(query));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete customer",
        description = "Deletes a customer record by their ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Customer deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Customer Not Found",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 404,
                                "message": "Customer with ID 123 not found",
                                "path": "/api/v1/customers/123"
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer with ID " + id + " has been deleted successfully.");
    }

    @PostMapping(path = "/{id}/signature", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload customer signature",
        description = "Uploads a signature image for a specific customer"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Signature uploaded successfully",
            content = @Content(schema = @Schema(implementation = CustomerSignatureResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid file format or size",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Invalid File Format",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "Invalid file type. Expected image file, got: application/pdf",
                                "path": "/api/v1/customers/123/signature"
                            }"""
                    ),
                    @ExampleObject(
                        name = "File Too Large",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 400,
                                "message": "File size exceeds maximum limit of 5MB",
                                "path": "/api/v1/customers/123/signature"
                            }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Customer Not Found",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 404,
                                "message": "Customer with ID 123 not found",
                                "path": "/api/v1/customers/123/signature"
                            }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error processing the signature file",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Processing Error",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 500,
                                "message": "Error processing signature file: Invalid image format",
                                "path": "/api/v1/customers/123/signature"
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<CustomerSignatureResponse> uploadSignature(
            @PathVariable Long id,
            @RequestParam("signatureFile") MultipartFile signatureFile) {
        Customer updatedCustomer = customerService.updateCustomerSignature(id, signatureFile);
        return ResponseEntity.ok(new CustomerSignatureResponse(
                updatedCustomer.getFirstName(),
                updatedCustomer.getLastName(),
                updatedCustomer.getSignatureUrl()
        ));
    }

    @GetMapping("/{id}/signature")
    @Operation(
        summary = "Get customer signature",
        description = "Retrieves the signature image for a specific customer"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Signature image retrieved successfully",
            content = @Content(mediaType = "image/png")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer or signature not found",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Customer Not Found",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 404,
                                "message": "Customer with ID 123 not found",
                                "path": "/api/v1/customers/123/signature"
                            }"""
                    ),
                    @ExampleObject(
                        name = "Signature Not Found",
                        value = """
                            {
                                "timestamp": "2024-05-11T19:46:05.834+02:00",
                                "status": 404,
                                "message": "No signature found for customer with ID 123",
                                "path": "/api/v1/customers/123/signature"
                            }"""
                    )
                }
            )
        )
    })
    public ResponseEntity<byte[]> getCustomerSignature(@PathVariable Long id) {
        byte[] imageBytes = customerService.getCustomerSignature(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
