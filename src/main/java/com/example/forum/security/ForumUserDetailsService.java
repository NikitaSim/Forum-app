package com.example.forum.security;

import com.example.forum.models.User;
import com.example.forum.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Загружает пользователя из БД для Spring Security.
 * Роль из {@link com.example.forum.models.UserRole} превращается в authority ROLE_{имя роли}.
 */
@Slf4j
@Service
public class ForumUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public ForumUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username == null ? "" : username.trim())
                .orElseThrow(() -> {
                    log.warn("Попытка входа несуществующего пользователя '{}'", username);
                    return new UsernameNotFoundException("Пользователь не найден: " + username);
                });

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
