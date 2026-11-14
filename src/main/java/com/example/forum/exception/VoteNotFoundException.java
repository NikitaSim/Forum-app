package com.example.forum.exception;

public class VoteNotFoundException extends ResourceNotFoundException {
    public VoteNotFoundException(Long id) {
        super("Голос с ID " + id + " не найден.");
    }
}
