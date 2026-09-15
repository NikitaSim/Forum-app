package com.example.forum.repository;

import java.util.List;
import java.util.Optional;

import com.example.forum.models.*;

public interface PostRepository {

    Post create (Post post);
       
    Optional<Post> findById(Long id);

    List<Post> findAllByAuthorId(Long AuthorId);

    List<Post> findAllByThreadId(Long ThreadId);

    void deletePost(Long id);
}
