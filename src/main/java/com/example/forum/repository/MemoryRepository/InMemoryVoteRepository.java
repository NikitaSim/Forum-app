package com.example.forum.repository.MemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.forum.models.Vote;
import com.example.forum.repository.VoteRepository;

public class InMemoryVoteRepository implements VoteRepository {

    private final Map<Long, Vote> votes = new ConcurrentHashMap<>();

    @Override
    public Vote create(Vote vote) {
        votes.put(vote.getId(), vote);
        return vote;
    }

    @Override
    public Optional<Vote> findById(Long id) {
        return Optional.ofNullable(votes.get(id));
    }

    @Override
    public void deleteVote(Long id) {
        votes.remove(id);
    }

    @Override
    public List<Vote> findAllByPostId(Long postId) {
        List<Vote> resultList = new ArrayList<>();

        for (Vote vote : votes.values()) {
            if (vote.getPost().getId().equals(postId)) {
                resultList.add(vote);
            }
        }

        return resultList;
    }

    @Override
    public Optional<Vote> findByUserIdAndPostId(Long userId, Long postId) {
        for (Vote vote : votes.values()) {
            if (vote.getUser().getId().equals(userId)
                    && vote.getPost().getId().equals(postId)) {
                return Optional.of(vote);
            }
        }

        return Optional.empty();
    }
}