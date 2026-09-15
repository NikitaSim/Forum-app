package com.example.forum.repository.MemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.forum.models.Post;
import com.example.forum.repository.PostRepository;

public class InMemoryPostRepository implements PostRepository{

    final private Map<Long, Post> posts = new ConcurrentHashMap<>();

    @Override
    public Post create (Post post) {
        posts.put(post.getId(), post);
        return post;
    }
    
   @Override
    public List<Post> findAllByAuthorId(Long authorId) {
        List<Post> resultList = new ArrayList<>();

        for (Post post : posts.values()) {
            if (post.getAuthor().getId().equals(authorId)) {
                resultList.add(post);
            }
        }

        return resultList;
    }
    @Override
    public List<Post> findAllByThreadId(Long threadId) {
        List<Post> resultList = new ArrayList<>();

        for (Post post : posts.values()) {
            if (post.getThread().getId().equals(threadId)) {
                resultList.add(post);
            }
        }

        return resultList;
    }

    @Override 
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }

    @Override
    public void deletePost(Long id) {
        posts.remove(id);
    }
}
