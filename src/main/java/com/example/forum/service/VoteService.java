package com.example.forum.service;

import com.example.forum.models.*;
import com.example.forum.exception.UserNotFoundException;
import com.example.forum.exception.PostNotFoundException;
import com.example.forum.exception.VoteNotFoundException;
import com.example.forum.exception.InvalidRequestException;
import com.example.forum.exception.DuplicateResourceException;
import com.example.forum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
//@RequiredArgsConstructor
@Transactional
public class VoteService {
    private final VoteRepository repository;
    private final UserRepository users;
    private final PostRepository posts;

    public VoteService(PostRepository posts, VoteRepository repository, UserRepository users) {
        this.posts = posts;
        this.repository = repository;
        this.users = users;
    }

    public Vote create(Vote vote) {
        if(vote==null||vote.getType()==null) throw new InvalidRequestException("Необходимо указать тип голоса: UP или DOWN.");
        if(vote.getUser()==null||vote.getUser().getId()==null) throw new InvalidRequestException("Необходимо указать пользователя.");
        if(vote.getPost()==null||vote.getPost().getId()==null) throw new InvalidRequestException("Необходимо указать пост.");
        if(repository.findByUserIdAndPostId(vote.getUser().getId(), vote.getPost().getId()).isPresent()) throw new DuplicateResourceException("Этот пользователь уже голосовал за данный пост.");

        vote.setUser(users.findById(vote.getUser().getId()).orElseThrow(() -> new UserNotFoundException(vote.getUser().getId())));
        vote.setPost(posts.findById(vote.getPost().getId()).orElseThrow(() -> new PostNotFoundException(vote.getPost().getId())));
        return repository.save(vote);
    }
    @Transactional(readOnly=true)
    public List<Vote> findAll(Long postId) { return postId==null?repository.findAll():repository.findAllByPostId(postId); }

    @Transactional(readOnly=true)
    public Vote find(Long id) { return repository.findById(id).orElseThrow(() -> new VoteNotFoundException(id)); }
    public Vote update(Long id, Vote input) { Vote v=find(id); if(input.getType()==null) throw new InvalidRequestException("Необходимо указать тип голоса: UP или DOWN."); v.setType(input.getType()); return repository.save(v); }
    public void delete(Long id) { repository.delete(find(id)); }
}
