package com.example.forum.exception;

public class PostNotFoundException extends ResourceNotFoundException {
    public PostNotFoundException(Long id) {
        super("Пост с ID " + id + " не найден.");
    }
}
