package com.validata.deepdetect.model;

import jakarta.persistence.*;
import org.mapstruct.Builder;

@Entity
@Table
public class Customer {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id ;
    private String firstName;
    private String lastName;
    private String signatureUrl;

    public Customer() {
    }
    public Customer(Long id, String firstName, String lastName, String signatureUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.signatureUrl = signatureUrl;
    }
    public Customer( String firstName, String lastName, String signatureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.signatureUrl = signatureUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", signatureUrl='" + signatureUrl + '\'' +
                '}';
    }
}
