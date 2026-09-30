package com.example.forum.models;

public class Vote {

    private Long id;

    private User user;
    private Post post;
    private VoteType type;

    public Vote() {
    }

    public Vote(Long id, User user, Post post, VoteType type) {
        this.id = id;
        this.user = user;
        this.post = post;
        this.type = type;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public VoteType getType() {
		return type;
	}

	public void setType(VoteType type) {
		this.type = type;
	}

}
