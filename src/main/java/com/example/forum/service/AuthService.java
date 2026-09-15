package com.example.forum.service;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.UserRepository;

public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public User register(
            Long id,
            String username,
            String email,
            String password
    ) {
        validateRegistration(
                id,
                username,
                email,
                password
        );

        if (userRepository.findById(id).isPresent()) {
            throw new IllegalStateException(
                    "User with id " + id + " already exists"
            );
        }

        User user = new User(
                id,
                username.trim(),
                email.trim(),
                password,
                UserRole.USER
        );

        return userService.createUser(user);
    }

    public User login(Long id, String password) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Id must be a positive number"
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid credentials"
                        )
                );

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException(
                    "Invalid credentials"
            );
        }

        return user;
    }

    private void validateRegistration(
            Long id,
            String username,
            String email,
            String password
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Id must be a positive number"
            );
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty"
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty"
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters"
            );
        }
    }
}