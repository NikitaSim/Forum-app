package com.example.forum.repository;

import com.example.forum.models.ForumThread;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThreadRepository extends JpaRepository<ForumThread, Long> {
    @Override
    @EntityGraph(attributePaths = {"author", "tags"})
    List<ForumThread> findAll();

    @Override
    @EntityGraph(attributePaths = {"author", "tags"})
    Optional<ForumThread> findById(Long id);

    @EntityGraph(attributePaths = {"author", "tags"})
    List<ForumThread> findAllByAuthorId(Long authorId);

    @EntityGraph(attributePaths = {"author", "tags"})
    List<ForumThread> findAllByTitleContainingIgnoreCase(String title);
}
