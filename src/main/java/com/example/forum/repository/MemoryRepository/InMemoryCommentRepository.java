package com.example.forum.repository.MemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.forum.models.Comment;
import com.example.forum.repository.CommentRepository;

public class InMemoryCommentRepository implements CommentRepository {

    private final Map<Long, Comment> comments = new ConcurrentHashMap<>();

    @Override
    public Comment create(Comment comment) {
        comments.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public List<Comment> findAllByAuthorId(Long authorId) {
        List<Comment> resultList = new ArrayList<>();

        for (Comment comment : comments.values()) {
            if (comment.getAuthor().getId().equals(authorId)) {
                resultList.add(comment);
            }
        }
        return resultList;
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        List<Comment> resultList = new ArrayList<>();

        for (Comment comment : comments.values()) {
            if (comment.getPost().getId().equals(postId)) {
                resultList.add(comment);
            }
        }
        return resultList;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(comments.get(id));
    }

    @Override
    public void deleteComment(Long id) {
        comments.remove(id);
    }
}