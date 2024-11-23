package com.backend.ecommerce_backend.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {
    JWTRequestFilter jwtRequestFilter;

    /**
     * Constructor.
     *
     * @param jwtRequestFilter injected by spring boot
     *
     */
    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF protection
        http.csrf(AbstractHttpConfigurer::disable)
                // Disable CORS
                .cors(AbstractHttpConfigurer::disable);

        // Add JWT request filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Configure authorization rules
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                // Allow unauthenticated access to specific endpoints
                .requestMatchers("/product", "/auth/login", "/auth/register",
                        "/auth/verify", "/auth/forgot", "/auth/reset", "/error",
                        "websocket","websocket/**").permitAll()
                // Require authentication for all other requests
                .anyRequest().authenticated());

        // Build and return the SecurityFilterChain
        return http.build();
    }
}
