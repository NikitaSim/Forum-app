package com.example.forum.repository;

import com.example.forum.models.*;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(User user);
    
    Optional<User> findById(Long id);

    List<User> showAllUsers();

    void deleteUser(Long id);
}