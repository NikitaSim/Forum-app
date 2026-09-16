package com.example.forum.console;

import com.example.forum.models.User;

public class Session {
    private User currentUser;

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        return currentUser;
    }

    public void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }
}
