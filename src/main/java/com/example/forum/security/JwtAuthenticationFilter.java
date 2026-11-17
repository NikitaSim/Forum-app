package com.example.forum.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Читает заголовок {@code Authorization: Bearer <token>}, проверяет токен и кладёт
 * пользователя в SecurityContext. При неверном токене запрос остаётся неаутентифицированным
 * и получает 401. Роль берётся из БД при каждом запросе, поэтому её смена действует сразу.
 * Не помечен как @Component намеренно: фильтр создаётся в SecurityConfig.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(PREFIX.length()).trim();
            Optional<String> username = jwtService.extractUsername(token);
            if (username.isPresent()) {
                try {
                    UserDetails details = userDetailsService.loadUserByUsername(username.get());
                    SecurityContextHolder.getContext().setAuthentication(
                            UsernamePasswordAuthenticationToken.authenticated(
                                    details, null, details.getAuthorities()));
                    log.debug("JWT принят для пользователя '{}'", username.get());
                } catch (UsernameNotFoundException e) {
                    log.warn("JWT выдан несуществующему пользователю '{}'", username.get());
                }
            } else {
                log.warn("Получен неверный или просроченный JWT ({} {})",
                        request.getMethod(), request.getRequestURI());
            }
        }
        chain.doFilter(request, response);
    }
}
