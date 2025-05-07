package com.validata.deepdetect.controller;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.dto.CustomerSignatureResponse;
import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(customerRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(customerService.searchCustomers(query));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer with ID " + id + " has been deleted successfully.");
    }

    @PostMapping("/{id}/signature")
    public ResponseEntity<CustomerSignatureResponse> uploadSignature(
            @PathVariable Long id,
            @RequestParam("signatureFile") MultipartFile signatureFile){try {

        Customer updatedCustomer = customerService.updateCustomerSignature(id, signatureFile);
        return ResponseEntity.ok(new CustomerSignatureResponse(
                updatedCustomer.getFirstName(),
                updatedCustomer.getLastName(),
                updatedCustomer.getSignatureUrl()
        ));
    } catch (Exception e) {
        return ResponseEntity.status(500)
                .body(new CustomerSignatureResponse("Error", "Error", "Error"));
    }
    }


    @GetMapping("/{id}/signature")
    public ResponseEntity<byte[]> getCustomerSignature(@PathVariable Long id) {
        byte[] imageBytes = customerService.getCustomerSignature(id);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/png") // أو "image/jpeg" حسب نوع الصورة
                .body(imageBytes);
    }

}
