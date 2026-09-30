package com.example.forum.models;

import java.util.ArrayList;
import java.util.List;

public class ForumThread {
    
    private Long id;
    
    private User author;
    private String title;

    final private List<String> tags = new ArrayList<>();
    final private List<Post> posts = new ArrayList<>();

	public ForumThread(){
    }

    public ForumThread(Long id, User author, String title) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    public List<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

     public List<Post> getPosts() {
		return posts;
	}
}
