package com.example.forum.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    
    private UserRole role;

    final private List<ForumThread> threads = new ArrayList<>();
    final private List<Post> posts = new ArrayList<>();
    final private List<Comment> comments = new ArrayList<>();

	public User(){
    }

    public User(Long id, String username, String email, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<ForumThread> getThreads() {
		return threads;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public List<Comment> getComments() {
		return comments;
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
