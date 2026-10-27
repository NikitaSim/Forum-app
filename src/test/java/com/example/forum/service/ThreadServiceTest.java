package com.example.forum.service;

import com.example.forum.models.ForumThread;
import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.TagRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadServiceTest {
    @Mock ThreadRepository repository;
    @Mock UserRepository userRepository;
    @Mock TagRepository tagRepository;
    @InjectMocks ThreadService service;

    @Test
    void createLoadsAuthorAndSavesThread() {
        User author = new User(1L, "nikita", "n@mail.ru", "hash", UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(repository.save(any(ForumThread.class))).thenAnswer(inv -> inv.getArgument(0));

        ForumThread result = service.create(new ForumThread(null, new User(1L, null, null, null, null), "Go"));

        assertEquals("Go", result.getTitle());
        assertSame(author, result.getAuthor());
        verify(repository).save(result);
    }

    @Test
    void createRejectsBlankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(new ForumThread(null, new User(1L, null, null, null, null), " ")));
        verifyNoInteractions(repository, userRepository);
    }
}
