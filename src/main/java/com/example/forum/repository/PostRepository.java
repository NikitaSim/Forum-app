package com.example.forum.repository;

import com.example.forum.models.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    @EntityGraph(attributePaths = {"author", "thread"})
    List<Post> findAll();

    @Override
    @EntityGraph(attributePaths = {"author", "thread"})
    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = {"author", "thread"})
    List<Post> findAllByThreadId(Long threadId);

    @EntityGraph(attributePaths = {"author", "thread"})
    List<Post> findAllByAuthorId(Long authorId);
}
