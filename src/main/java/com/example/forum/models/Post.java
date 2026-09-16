package com.example.forum.models;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private Long id;

    private ForumThread thread;
    private User author;

    private String content;

    private final List<Comment> comments = new ArrayList<>();
    private final List<Vote> votes = new ArrayList<>();

    public Post() {
    }

    public Post(Long id, String content, User author, ForumThread thread) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.thread = thread;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ForumThread getThread() {
		return thread;
	}

	public void setThread(ForumThread thread) {
		this.thread = thread;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public List<Vote> getVotes() {
		return votes;
	}

}
