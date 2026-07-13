package com.example.AtivHub.AtivHub.infra.exception;

public class ProfessorNotFoundException extends RuntimeException {
    public ProfessorNotFoundException(String message) {
        super(message);
    }
}
