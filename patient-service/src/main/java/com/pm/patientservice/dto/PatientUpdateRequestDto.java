package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class PatientUpdateRequestDto {
    @Size(max = 100, min = 2, message = "the name should be min 2 characters and less than 100")
    private String name;

    @Email(message = "should be valid email xx@example.com")
    private String email;

    @Size(max = 100, min = 2, message = "the address should be min 2 characters and less than 100")
    private String address;

    private String dateOfBirth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
