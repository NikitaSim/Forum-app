package com.example.forum.models;

public class Comment {

    private Long id;
    private String content;

    private User author;
    private Post post;

    public Comment() {
    }

    public Comment(Long id, String content, User author, Post post) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.post = post;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
}