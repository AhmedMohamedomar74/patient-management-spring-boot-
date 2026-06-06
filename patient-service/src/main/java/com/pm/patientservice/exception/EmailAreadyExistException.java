package com.pm.patientservice.exception;

public class EmailAreadyExistException extends RuntimeException {
    public EmailAreadyExistException(String message) {
        super(message);
        
    }
}
