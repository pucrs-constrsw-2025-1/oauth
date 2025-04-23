package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.LoginRequestDTO;
import com.constrsw.oauth.dto.RegisterRequestDTO;
import com.constrsw.oauth.dto.TokenResponseDTO;
import com.constrsw.oauth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok().body("Usuário registrado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        TokenResponseDTO tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }
}