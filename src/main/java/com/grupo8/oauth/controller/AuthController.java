package com.grupo8.oauth.controller;

import com.grupo8.oauth.dto.LoginRequest;
import com.grupo8.oauth.dto.LoginResponse;
import com.grupo8.oauth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@ModelAttribute LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
