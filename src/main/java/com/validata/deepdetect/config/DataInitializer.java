package com.validata.deepdetect.config;

import com.validata.deepdetect.model.Customer;
import com.validata.deepdetect.model.Role;
import com.validata.deepdetect.model.User;
import com.validata.deepdetect.repository.CustomerRepository;
import com.validata.deepdetect.repository.RoleRepository;
import com.validata.deepdetect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Initialize roles if they don't exist
        if (roleRepository.count() == 0) {
            Role userRole = Role.builder()
                    .name("USER")
                    .build();
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .build();
            roleRepository.saveAll(List.of(userRole, adminRole));
        }

        // Initialize users if they don't exist
        if (userRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();
            String password = "$2a$10$4hpvtGQZSFwKW/kcyGcY.exxOsLJOZgCdn4fRLMkYajDgXLTsgEOG";

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            List<User> users = List.of(
                User.builder()
                    .firstName("Mohamed")
                    .lastName("Ahmed")
                    .email("mohamed.ahmed@example.com")
                    .password(password)
                    .createdAt(now)
                    .updatedAt(now)
                    .roles(List.of(adminRole))
                    .build(),
                User.builder()
                    .firstName("Ahmed")
                    .lastName("Hassan")
                    .email("ahmed.hassan@example.com")
                    .password(password)
                    .createdAt(now)
                    .updatedAt(now)
                    .roles(List.of(userRole))
                    .build(),
                User.builder()
                    .firstName("Khalid")
                    .lastName("Meaz")
                    .email("khalid.meaz@example.com")
                    .password(password)
                    .createdAt(now)
                    .updatedAt(now)
                    .roles(List.of(userRole))
                    .build(),
                User.builder()
                    .firstName("Ziad")
                    .lastName("Kamal")
                    .email("ziad.kamal@example.com")
                    .password(password)
                    .createdAt(now)
                    .updatedAt(now)
                    .roles(List.of(userRole))
                    .build(),
                User.builder()
                    .firstName("Amr")
                    .lastName("Youssef")
                    .email("amr.youssef@example.com")
                    .password(password)
                    .createdAt(now)
                    .updatedAt(now)
                    .roles(List.of(userRole))
                    .build(),
                User.builder()
                    .firstName("Omar")
                    .lastName("Ibrahim")
                    .email("omar.ibrahim@example.com")
                    .password(password)
                    .createdAt(now)
                    .updatedAt(now)
                    .roles(List.of(userRole))
                    .build()
            );
            userRepository.saveAll(users);
        }

        // Initialize customers if they don't exist
        if (customerRepository.count() == 0) {
            List<Customer> customers = List.of(
                Customer.builder()
                    .firstName("Mohamed")
                    .lastName("Ali")
                    .signatureUrl("https://deep-detect-bucket.fra1.cdn.digitaloceanspaces.com/sign-1.jpg")
                    .build(),
                Customer.builder()
                    .firstName("Ahmed")
                    .lastName("Hassan")
                    .signatureUrl("https://deep-detect-bucket.fra1.cdn.digitaloceanspaces.com/sign-2.jpg")
                    .build(),
                Customer.builder()
                    .firstName("Khalid")
                    .lastName("Saeed")
                    .signatureUrl("https://deep-detect-bucket.fra1.cdn.digitaloceanspaces.com/sign-1.jpg")
                    .build(),
                Customer.builder()
                    .firstName("Ziad")
                    .lastName("Mohamed")
                    .signatureUrl("https://deep-detect-bucket.fra1.cdn.digitaloceanspaces.com/sign-2.jpg")
                    .build(),
                Customer.builder()
                    .firstName("Amr")
                    .lastName("Youssef")
                    .signatureUrl("https://deep-detect-bucket.fra1.cdn.digitaloceanspaces.com/sign-1.jpg")
                    .build(),
                Customer.builder()
                    .firstName("Omar")
                    .lastName("Ibrahim")
                    .signatureUrl("https://deep-detect-bucket.fra1.cdn.digitaloceanspaces.com/sign-1.jpg")
                    .build()
            );
            customerRepository.saveAll(customers);
        }
    }
} 