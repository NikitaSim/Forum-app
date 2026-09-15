package com.example.forum.service;

import java.util.List;
import java.util.Optional;

import com.example.forum.models.User;
import com.example.forum.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        validateUser(user);

        if (userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalStateException(
                    "User with id " + user.getId() + " already exists"
            );
        }

        return userRepository.create(user);
    }

    public Optional<User> getUser(Long id) {
        validateId(id);
        return userRepository.findById(id);
    }

    public List<User> showUsers() {
        return userRepository.showAllUsers();
    }

    public void deleteUser(Long id) {
        validateId(id);

        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "User with id " + id + " not found"
            );
        }

        userRepository.deleteUser(id);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        validateId(user.getId());

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty"
            );
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty"
            );
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "User role cannot be null"
            );
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Id must be a positive number"
            );
        }
    }
}