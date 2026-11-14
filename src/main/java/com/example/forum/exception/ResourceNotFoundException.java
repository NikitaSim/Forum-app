package com.example.forum.exception;

public abstract class ResourceNotFoundException extends IllegalArgumentException {
    protected ResourceNotFoundException(String message) {
        super(message);
    }
}
