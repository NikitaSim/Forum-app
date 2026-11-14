package com.example.forum.exception;

public class CommentNotFoundException extends ResourceNotFoundException {
    public CommentNotFoundException(Long id) {
        super("Комментарий с ID " + id + " не найден.");
    }
}
