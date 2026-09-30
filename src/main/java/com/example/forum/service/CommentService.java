package com.example.forum.service;

import java.util.List;
import java.util.Optional;

import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.UserRepository;

public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            PostRepository postRepository
    ) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Comment createComment(Comment comment) {
        validateComment(comment);

        if (commentRepository.findById(comment.getId()).isPresent()) {
            throw new IllegalStateException(
                    "Comment with id " + comment.getId() + " already exists"
            );
        }

        Long authorId = comment.getAuthor().getId();
        Long postId = comment.getPost().getId();

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Author with id " + authorId + " not found"
                ));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Post with id " + postId + " not found"
                ));

        Comment createdComment = commentRepository.create(comment);

        author.getComments().add(createdComment);
        post.getComments().add(createdComment);

        return createdComment;
    }

    public Optional<Comment> getComment(Long id) {
        validateId(id);

        return commentRepository.findById(id);
    }

    public List<Comment> showAuthorComments(Long authorId) {
        validateId(authorId);

        if (userRepository.findById(authorId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Author with id " + authorId + " not found"
            );
        }

        return commentRepository.findAllByAuthorId(authorId);
    }

    public List<Comment> showPostComments(Long postId) {
        validateId(postId);

        if (postRepository.findById(postId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Post with id " + postId + " not found"
            );
        }

        return commentRepository.findAllByPostId(postId);
    }

    public void deleteComment(Long id) {
        validateId(id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Comment with id " + id + " not found"
                ));

        if (comment.getAuthor() != null) {
            comment.getAuthor().getComments().remove(comment);
        }

        if (comment.getPost() != null) {
            comment.getPost().getComments().remove(comment);
        }

        commentRepository.deleteComment(id);
    }

    private void validateComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException(
                    "Comment cannot be null"
            );
        }

        validateId(comment.getId());

        if (comment.getContent() == null ||
                comment.getContent().isBlank()) {

            throw new IllegalArgumentException(
                    "Comment content cannot be empty"
            );
        }

        if (comment.getAuthor() == null ||
                comment.getAuthor().getId() == null) {

            throw new IllegalArgumentException(
                    "Comment author cannot be null"
            );
        }

        if (comment.getPost() == null ||
                comment.getPost().getId() == null) {

            throw new IllegalArgumentException(
                    "Comment post cannot be null"
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