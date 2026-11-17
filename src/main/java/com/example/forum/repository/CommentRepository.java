package com.example.forum.repository;

import com.example.forum.models.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    @EntityGraph(attributePaths = {"author", "post"})
    List<Comment> findAll();

    @Override
    @EntityGraph(attributePaths = {"author", "post"})
    Optional<Comment> findById(Long id);

    @EntityGraph(attributePaths = {"author", "post"})
    List<Comment> findAllByPostId(Long postId);

    @EntityGraph(attributePaths = {"author", "post"})
    List<Comment> findAllByAuthorId(Long authorId);
}
