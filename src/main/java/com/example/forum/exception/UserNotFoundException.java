package com.example.forum.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден.");
    }
}
