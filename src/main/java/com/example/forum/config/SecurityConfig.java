package com.example.forum.config;

import com.example.forum.models.UserRole;
import com.example.forum.security.JwtAuthenticationFilter;
import com.example.forum.security.JwtService;
import com.example.forum.security.RestAccessDeniedHandler;
import com.example.forum.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Настройка Spring Security: JWT (Bearer-токен) + роли из БД.
 *
 * <ul>
 *   <li>/api/auth/**, Swagger UI и OpenAPI-описание — доступны без авторизации;</li>
 *   <li>/api/users/** — только ADMIN (просмотр — ADMIN и MODERATOR);</li>
 *   <li>DELETE по тредам, постам, комментариям и голосам — ADMIN и MODERATOR;</li>
 *   <li>остальные операции API — любой аутентифицированный пользователь.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String ADMIN = UserRole.ADMIN.name();
    private static final String MODERATOR = UserRole.MODERATOR.name();

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            RestAuthenticationEntryPoint authenticationEntryPoint,
                                            RestAccessDeniedHandler accessDeniedHandler,
                                            JwtService jwtService,
                                            UserDetailsService userDetailsService) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                    UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(handling -> handling
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**", "/v3/api-docs", "/v3/api-docs/**",
                            "/swagger-ui.html", "/swagger-ui/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/**")
                            .hasAnyRole(ADMIN, MODERATOR)
                    .requestMatchers("/api/users", "/api/users/**").hasRole(ADMIN)
                    .requestMatchers(HttpMethod.DELETE, "/api/threads/**", "/api/posts/**",
                            "/api/comments/**", "/api/votes/**").hasAnyRole(ADMIN, MODERATOR)
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().authenticated());
        return http.build();
    }
}
