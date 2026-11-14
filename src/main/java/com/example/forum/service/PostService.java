package com.example.forum.service;

import com.example.forum.models.ForumThread;
import com.example.forum.exception.UserNotFoundException;
import com.example.forum.exception.ThreadNotFoundException;
import com.example.forum.exception.PostNotFoundException;
import com.example.forum.exception.InvalidRequestException;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
//@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository repository;
    private final UserRepository users;
    private final ThreadRepository threads;

    public PostService(PostRepository repository, ThreadRepository threads, UserRepository users) {
        this.repository = repository;
        this.threads = threads;
        this.users = users;
    }

    public Post create(Post post) {
        validate(post);
        User author=users.findById(post.getAuthor().getId()).orElseThrow(() -> new UserNotFoundException(post.getAuthor().getId()));
        ForumThread thread=threads.findById(post.getThread().getId()).orElseThrow(() -> new ThreadNotFoundException(post.getThread().getId()));
        post.setAuthor(author); post.setThread(thread); return repository.save(post);
    }
    @Transactional(readOnly=true)
    public List<Post> findAll(Long threadId, Long authorId) {
        if(threadId!=null) return repository.findAllByThreadId(threadId);
        if(authorId!=null) return repository.findAllByAuthorId(authorId);
        return repository.findAll();
    }
    @Transactional(readOnly=true)
    public Post find(Long id) { return repository.findById(id).orElseThrow(() -> new PostNotFoundException(id)); }
    public Post update(Long id, Post input) { Post p=find(id); if(input.getContent()==null||input.getContent().isBlank()) throw new InvalidRequestException("Содержимое поста обязательно."); p.setContent(input.getContent()); return repository.save(p); }
    public void delete(Long id) { repository.delete(find(id)); }
    private void validate(Post p) { if(p==null||p.getContent()==null||p.getContent().isBlank()) throw new InvalidRequestException("Содержимое поста обязательно."); if(p.getAuthor()==null||p.getAuthor().getId()==null) throw new InvalidRequestException("Необходимо указать автора поста."); if(p.getThread()==null||p.getThread().getId()==null) throw new InvalidRequestException("Необходимо указать тред для поста."); }
}
