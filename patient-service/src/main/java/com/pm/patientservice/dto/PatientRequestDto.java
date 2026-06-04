package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

public class PatientRequestDto {
    @NotNull(message = "the name should not be null")
    @Size(max = 100 , min = 2 , message = "the name should be min 2 characters and less than 100")
    private String name;

    @NotNull(message = "the email should not be null")
    @Email(message = "should be valid email xx@examble.com")
    private String email;

    @NotNull(message = "the address should not be null")
    @Size(max = 100 , min = 2 , message = "the name should be min 2 characters and less than 100")
    private String address;

    @NotNull(message = "the dateOfBirth should not be null")
    private String dateOfBirth;

    @NotNull(message = "the registeredDate should not be null")
    private String registeredDate;

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

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }


}
