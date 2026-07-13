package com.example.AtivHub.AtivHub.infra.exception;

public class StudentAlreadySubmittedException extends RuntimeException {
    public StudentAlreadySubmittedException(String message) {
        super(message);
    }
}
