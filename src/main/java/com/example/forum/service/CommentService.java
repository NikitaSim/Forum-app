package com.example.forum.service;

import com.example.forum.models.Comment;
import com.example.forum.exception.UserNotFoundException;
import com.example.forum.exception.PostNotFoundException;
import com.example.forum.exception.CommentNotFoundException;
import com.example.forum.exception.InvalidRequestException;
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
        if(c==null||c.getContent()==null||c.getContent().isBlank()) throw new InvalidRequestException("Содержимое комментария обязательно.");
        if(c.getAuthor()==null||c.getAuthor().getId()==null) throw new InvalidRequestException("Необходимо указать автора комментария.");
        if(c.getPost()==null||c.getPost().getId()==null) throw new InvalidRequestException("Необходимо указать пост для комментария.");
        c.setAuthor(users.findById(c.getAuthor().getId()).orElseThrow(() -> new UserNotFoundException(c.getAuthor().getId())));
        c.setPost(posts.findById(c.getPost().getId()).orElseThrow(() -> new PostNotFoundException(c.getPost().getId())));
        return repository.save(c);
    }
    @Transactional(readOnly=true)
    public List<Comment> findAll(Long postId, Long authorId) { if(postId!=null)return repository.findAllByPostId(postId); if(authorId!=null)return repository.findAllByAuthorId(authorId); return repository.findAll(); }
    @Transactional(readOnly=true)
    public Comment find(Long id) { return repository.findById(id).orElseThrow(() -> new CommentNotFoundException(id)); }
    public Comment update(Long id, Comment input) { Comment c=find(id); if(input.getContent()==null||input.getContent().isBlank()) throw new InvalidRequestException("Содержимое комментария обязательно."); c.setContent(input.getContent()); return repository.save(c); }
    public void delete(Long id) { repository.delete(find(id)); }
}
