package com.example.forum.repository.MemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.forum.models.ForumThread;
import com.example.forum.repository.ThreadRepository;

public class InMemoryThreadRepository implements ThreadRepository{

    final private  Map<Long, ForumThread> threads = new ConcurrentHashMap<>();

    @Override
    public ForumThread create (ForumThread thread) {
        threads.put(thread.getId(), thread);
        return thread;
    }

    @Override 
    public Optional<ForumThread> findById(Long id) {
        return Optional.ofNullable(threads.get(id));
    }

    @Override
    public List<ForumThread> showAllThreads() {
        return new ArrayList<>(threads.values());
    }

    @Override
    public void deleteThread(Long id) {
        threads.remove(id);
    }
    
}
