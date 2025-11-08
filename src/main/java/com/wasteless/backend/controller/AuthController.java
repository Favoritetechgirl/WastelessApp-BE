package com.wasteless.backend.controller;

import com.wasteless.backend.dto.AuthResponse;
import com.wasteless.backend.dto.LoginRequest;
import com.wasteless.backend.dto.RegisterRequest;
import com.wasteless.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") //allows frontend access for now
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest reqeust) {
        return ResponseEntity.ok(authService.register(reqeust));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@RequestBody LoginRequest reqeust) {
        return ResponseEntity.ok(authService.login(reqeust));
    }
}
