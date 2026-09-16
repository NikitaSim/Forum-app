package com.example.forum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.models.Vote;
import com.example.forum.models.VoteType;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;

public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            ThreadRepository threadRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.threadRepository = threadRepository;
    }

    public Post createPost(Post post) {
        validatePost(post);

        if (postRepository.findById(post.getId()).isPresent()) {
            throw new IllegalStateException(
                    "Post with id " + post.getId() + " already exists"
            );
        }

        Long authorId = post.getAuthor().getId();
        Long threadId = post.getThread().getId();

        if (userRepository.findById(authorId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Author does not exist"
            );
        }

        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Thread does not exist"
                ));

        Post createdPost = postRepository.create(post);

        post.getAuthor().getPosts().add(post);
        thread.getPosts().add(post);

        return createdPost;
    }

    public Optional<Post> getPost(Long id) {
        validateId(id);
        return postRepository.findById(id);
    }

    public List<Post> showAuthorPosts(Long authorId) {
        validateId(authorId);

        if (userRepository.findById(authorId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Author does not exist"
            );
        }

        return postRepository.findAllByAuthorId(authorId);
    }

    public List<Post> showThreadPosts(Long threadId) {
        validateId(threadId);

        if (threadRepository.findById(threadId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Thread does not exist"
            );
        }

        return postRepository.findAllByThreadId(threadId);
    }

    public void deletePost(Long id) {
        validateId(id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Post with id " + id + " not found"
                ));

        post.getAuthor().getPosts().remove(post);

        if (post.getThread() != null) {
            post.getThread().getPosts().remove(post);
        }

        postRepository.deletePost(id);
    }

    public List<Post> searchPosts(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be empty"
            );
        }

        String normalizedQuery = query.toLowerCase();

        List<Post> result = new ArrayList<>();

        for (ForumThread thread : threadRepository.showAllThreads()) {

            for (Post post : thread.getPosts()) {

                if (post.getContent() != null &&
                        post.getContent()
                                .toLowerCase()
                                .contains(normalizedQuery)) {

                    result.add(post);
                }
            }
        }

        return result;
    }

    public List<com.example.forum.models.Comment> getPostComments(Long postId) {
        Post post = getExistingPost(postId);

        return List.copyOf(post.getComments());
    }

    public int getPostRating(Long postId) {
        Post post = getExistingPost(postId);

        int rating = 0;

        for (Vote vote : post.getVotes()) {
            if (vote.getType() == VoteType.UP) {
                rating++;
            } else if (vote.getType() == VoteType.DOWN) {
                rating--;
            }
        }

        return rating;
    }

    private Post getExistingPost(Long id) {
        validateId(id);

        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Post with id " + id + " not found"
                ));
    }

    private void validatePost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException(
                    "Post cannot be null"
            );
        }

        validateId(post.getId());

        if (post.getContent() == null ||
                post.getContent().isBlank()) {

            throw new IllegalArgumentException(
                    "Post content cannot be empty"
            );
        }

        if (post.getAuthor() == null ||
                post.getAuthor().getId() == null) {

            throw new IllegalArgumentException(
                    "Post author cannot be null"
            );
        }

        if (post.getThread() == null ||
                post.getThread().getId() == null) {

            throw new IllegalArgumentException(
                    "Post thread cannot be null"
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