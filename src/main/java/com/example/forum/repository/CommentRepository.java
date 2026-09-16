package com.example.forum.repository;

import java.util.List;
import java.util.Optional;

import com.example.forum.models.Comment;

public interface CommentRepository {

    Comment create (Comment comment);
       
    Optional<Comment> findById(Long id);

    List<Comment> findAllByAuthorId(Long AuthorId);

    List<Comment> findAllByPostId(Long PostId);

    void deleteComment(Long id);
}