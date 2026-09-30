package com.example.forum.repository;

import java.util.List;
import java.util.Optional;

import com.example.forum.models.ForumThread;

public interface ThreadRepository {

    ForumThread create (ForumThread thread);
       
    Optional<ForumThread> findById(Long id);

    List<ForumThread> showAllThreads();

    void deleteThread(Long id);

}
