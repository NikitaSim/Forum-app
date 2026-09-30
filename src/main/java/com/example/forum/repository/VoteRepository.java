package com.example.forum.repository;

import java.util.List;
import java.util.Optional;

import com.example.forum.models.Vote;

public interface VoteRepository {

    Vote create(Vote vote);

    Optional<Vote> findById(Long id);

    void deleteVote(Long id);

    List<Vote> findAllByPostId(Long postId);

    Optional<Vote> findByUserIdAndPostId(Long userId, Long postId);
}