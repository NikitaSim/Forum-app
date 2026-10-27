package com.example.forum.repository;

import com.example.forum.models.Vote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Override
    @EntityGraph(attributePaths = {"user", "post"})
    List<Vote> findAll();

    @Override
    @EntityGraph(attributePaths = {"user", "post"})
    Optional<Vote> findById(Long id);

    @EntityGraph(attributePaths = {"user", "post"})
    List<Vote> findAllByPostId(Long postId);

    Optional<Vote> findByUserIdAndPostId(Long userId, Long postId);
}
