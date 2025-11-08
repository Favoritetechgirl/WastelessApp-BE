package com.wasteless.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/health").permitAll() // public endpoints
                        .anyRequest().authenticated() // protect others
                )
                .formLogin(form -> form.disable()) // disable login form
                .httpBasic(basic -> basic.disable()); // disable basic auth

        return http.build();
    }
}
