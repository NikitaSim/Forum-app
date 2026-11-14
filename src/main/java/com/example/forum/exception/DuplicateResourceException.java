package com.example.forum.exception;

public class DuplicateResourceException extends IllegalArgumentException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
