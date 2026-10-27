package com.example.forum.service;

import com.example.forum.models.Comment;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
//@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository repository;
    private final UserRepository users;
    private final PostRepository posts;

    public CommentService(PostRepository posts, CommentRepository repository, UserRepository users) {
        this.posts = posts;
        this.repository = repository;
        this.users = users;
    }

    public Comment create(Comment c) {
        if(c==null||c.getContent()==null||c.getContent().isBlank()) throw new IllegalArgumentException("Content is required");
        if(c.getAuthor()==null||c.getAuthor().getId()==null) throw new IllegalArgumentException("Author is required");
        if(c.getPost()==null||c.getPost().getId()==null) throw new IllegalArgumentException("Post is required");
        c.setAuthor(users.findById(c.getAuthor().getId()).orElseThrow(() -> new IllegalArgumentException("Author not found")));
        c.setPost(posts.findById(c.getPost().getId()).orElseThrow(() -> new IllegalArgumentException("Post not found")));
        return repository.save(c);
    }
    @Transactional(readOnly=true)
    public List<Comment> findAll(Long postId, Long authorId) { if(postId!=null)return repository.findAllByPostId(postId); if(authorId!=null)return repository.findAllByAuthorId(authorId); return repository.findAll(); }
    @Transactional(readOnly=true)
    public Comment find(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Comment not found")); }
    public Comment update(Long id, Comment input) { Comment c=find(id); if(input.getContent()==null||input.getContent().isBlank()) throw new IllegalArgumentException("Content is required"); c.setContent(input.getContent()); return repository.save(c); }
    public void delete(Long id) { repository.delete(find(id)); }
}
