package com.example.forum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;

public class ThreadService {

    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;

    public ThreadService(
            ThreadRepository threadRepository,
            UserRepository userRepository
    ) {
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
    }

    public ForumThread createThread(ForumThread thread) {
        validateThread(thread);

        if (threadRepository.findById(thread.getId()).isPresent()) {
            throw new IllegalStateException(
                    "Thread with id " + thread.getId() + " already exists"
            );
        }

        if (userRepository.findById(thread.getAuthor().getId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Author does not exist"
            );
        }

        ForumThread createdThread = threadRepository.create(thread);

        thread.getAuthor().getThreads().add(thread);

        return createdThread;
    }

    public Optional<ForumThread> getThread(Long id) {
        validateId(id);
        return threadRepository.findById(id);
    }

    public List<ForumThread> showThreads() {
        return threadRepository.showAllThreads();
    }

    public void deleteThread(Long id) {
        validateId(id);

        ForumThread thread = threadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Thread with id " + id + " not found"
                ));

        thread.getAuthor().getThreads().remove(thread);

        threadRepository.deleteThread(id);
    }

    public List<ForumThread> searchThreads(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be empty"
            );
        }

        String normalizedQuery = query.toLowerCase();

        List<ForumThread> result = new ArrayList<>();

        for (ForumThread thread : threadRepository.showAllThreads()) {
            if (thread.getTitle() != null &&
                    thread.getTitle()
                            .toLowerCase()
                            .contains(normalizedQuery)) {

                result.add(thread);
            }
        }

        return result;
    }

    public void addTag(Long threadId, String tag) {
        ForumThread thread = getExistingThread(threadId);

        validateTag(tag);

        String normalizedTag = tag.trim();

        if (!thread.getTags().contains(normalizedTag)) {
            thread.addTag(normalizedTag);
        }
    }

    public void removeTag(Long threadId, String tag) {
        ForumThread thread = getExistingThread(threadId);

        validateTag(tag);

        thread.removeTag(tag.trim());
    }

    public List<String> getTags(Long threadId) {
        ForumThread thread = getExistingThread(threadId);

        return List.copyOf(thread.getTags());
    }

    public List<Post> getThreadPosts(Long threadId) {
        ForumThread thread = getExistingThread(threadId);

        return List.copyOf(thread.getPosts());
    }

    private ForumThread getExistingThread(Long id) {
        validateId(id);

        return threadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Thread with id " + id + " not found"
                ));
    }

    private void validateThread(ForumThread thread) {
        if (thread == null) {
            throw new IllegalArgumentException(
                    "Thread cannot be null"
            );
        }

        validateId(thread.getId());

        if (thread.getTitle() == null ||
                thread.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Thread title cannot be empty"
            );
        }

        if (thread.getAuthor() == null ||
                thread.getAuthor().getId() == null) {

            throw new IllegalArgumentException(
                    "Thread author cannot be null"
            );
        }
    }

    private void validateTag(String tag) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException(
                    "Tag cannot be empty"
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