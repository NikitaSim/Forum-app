package com.example.forum.service;

import java.util.List;
import java.util.Optional;

import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.Vote;
import com.example.forum.models.VoteType;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.UserRepository;
import com.example.forum.repository.VoteRepository;

public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public VoteService(
            VoteRepository voteRepository,
            UserRepository userRepository,
            PostRepository postRepository
    ) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public synchronized Vote createVote(Vote vote) {
        validateVote(vote);

        User user = userRepository.findById(vote.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User does not exist"
                ));

        Post post = postRepository.findById(vote.getPost().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Post does not exist"
                ));

        Optional<Vote> existingVote =
                voteRepository.findByUserIdAndPostId(
                        user.getId(),
                        post.getId()
                );

        if (existingVote.isPresent()) {
            throw new IllegalStateException(
                    "User has already voted for this post"
            );
        }

        Vote createdVote = voteRepository.create(vote);

        post.getVotes().add(createdVote);

        return createdVote;
    }

    public synchronized Vote changeVote(
            Long userId,
            Long postId,
            VoteType newType
    ) {
        validateId(userId);
        validateId(postId);

        if (newType == null) {
            throw new IllegalArgumentException(
                    "Vote type cannot be null"
            );
        }

        Vote vote = voteRepository
                .findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vote does not exist"
                ));

        vote.setType(newType);

        return vote;
    }

    public Optional<Vote> getVote(Long id) {
        validateId(id);
        return voteRepository.findById(id);
    }

    public List<Vote> showPostVotes(Long postId) {
        validateId(postId);

        if (postRepository.findById(postId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Post does not exist"
            );
        }

        return voteRepository.findAllByPostId(postId);
    }

    public Optional<Vote> getUserPostVote(
            Long userId,
            Long postId
    ) {
        validateId(userId);
        validateId(postId);

        return voteRepository.findByUserIdAndPostId(
                userId,
                postId
        );
    }

    public void deleteVote(Long id) {
        validateId(id);

        Vote vote = voteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vote with id " + id + " not found"
                ));

        if (vote.getPost() != null) {
            vote.getPost().getVotes().remove(vote);
        }

        voteRepository.deleteVote(id);
    }

    public int getPostRating(Long postId) {
        validateId(postId);

        if (postRepository.findById(postId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Post does not exist"
            );
        }

        int rating = 0;

        for (Vote vote : voteRepository.findAllByPostId(postId)) {

            if (vote.getType() == VoteType.UP) {
                rating++;
            } else if (vote.getType() == VoteType.DOWN) {
                rating--;
            }
        }

        return rating;
    }

    public long countUpVotes(Long postId) {
        return voteRepository.findAllByPostId(postId)
                .stream()
                .filter(vote -> vote.getType() == VoteType.UP)
                .count();
    }

    public long countDownVotes(Long postId) {
        return voteRepository.findAllByPostId(postId)
                .stream()
                .filter(vote -> vote.getType() == VoteType.DOWN)
                .count();
    }

    private void validateVote(Vote vote) {
        if (vote == null) {
            throw new IllegalArgumentException(
                    "Vote cannot be null"
            );
        }

        validateId(vote.getId());

        if (vote.getUser() == null ||
                vote.getUser().getId() == null) {

            throw new IllegalArgumentException(
                    "Vote user cannot be null"
            );
        }

        if (vote.getPost() == null ||
                vote.getPost().getId() == null) {

            throw new IllegalArgumentException(
                    "Vote post cannot be null"
            );
        }

        if (vote.getType() == null) {
            throw new IllegalArgumentException(
                    "Vote type cannot be null"
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