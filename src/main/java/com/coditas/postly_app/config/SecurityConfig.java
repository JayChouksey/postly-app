package com.coditas.postly_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui.html/**", "/v3/api-docs/**").permitAll()

                        // users
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole("ADMIN", "SUPER_ADMIN") // Get all users
                        .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated() // Get user by ID
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("MODERATOR")

                        // moderation
                        .requestMatchers("/api/moderation/**").hasAnyRole("MODERATOR","ADMIN","SUPER_ADMIN")

                        // posts (accessible to users and moderators/admins)
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").hasAnyRole("AUTHOR", "MODERATOR", "ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").hasRole("AUTHOR")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("AUTHOR","ADMIN")

                        // comments (users can CRUD, moderators/admins review)
                        .requestMatchers(HttpMethod.POST, "/api/comments/**").hasAnyRole("AUTHOR", "MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").hasAnyRole("AUTHOR", "MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/comments/**").hasAnyRole("AUTHOR", "MODERATOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasAnyRole("MODERATOR", "ADMIN")

                        // moderator requests
                        .requestMatchers(HttpMethod.POST, "/api/moderator-requests/request/**").hasRole("AUTHOR")
                        .requestMatchers(HttpMethod.GET, "/api/moderator-requests/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/moderator-requests/**").hasRole("ADMIN")

                        // admin requests
                        .requestMatchers(HttpMethod.POST, "/api/admin-requests/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin-requests/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin-requests/**").hasRole("SUPER_ADMIN")

                        // review logs (only admins/superadmins)
                        .requestMatchers("/api/review-logs/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
