package com.example.forum.exception;

public class InvalidCredentialsException extends IllegalArgumentException {
    public InvalidCredentialsException() {
        super("Неверное имя пользователя или пароль.");
    }
}
