package com.wasteless.backend.service;

import com.wasteless.backend.dto.AuthResponse;
import com.wasteless.backend.dto.LoginRequest;
import com.wasteless.backend.dto.RegisterRequest;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email already registered!", null);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();

        userRepository.save(user);

        return new AuthResponse("Registration successful!", null);
    }

    public  AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if(user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return new AuthResponse("Invalid credentials!", null);
        }

        //Later, we'll generate a JWT token here
        return new AuthResponse("Login successful!", "dummy-token");
    }
}
