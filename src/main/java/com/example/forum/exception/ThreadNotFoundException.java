package com.example.forum.exception;

public class ThreadNotFoundException extends ResourceNotFoundException {
    public ThreadNotFoundException(Long id) {
        super("Тред с ID " + id + " не найден.");
    }
}
