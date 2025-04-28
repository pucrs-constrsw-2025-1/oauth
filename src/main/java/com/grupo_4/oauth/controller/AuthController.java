package com.grupo_4.oauth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo_4.oauth.config.KeycloakConfig;
import com.grupo_4.oauth.model.LoginRequest;
import com.grupo_4.oauth.model.TokenResponse;
import com.grupo_4.oauth.service.KeycloakService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakService keycloakService;
    private final KeycloakConfig keycloakConfig;
    
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<TokenResponse> login(
            @RequestParam("client_id") String clientId,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("grant_type") String grantType) {
        
        log.info("Received login request for user: {}", username);
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setClient_id(clientId);
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        loginRequest.setGrant_type(grantType);
        
        TokenResponse tokenResponse = keycloakService.authenticate(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestParam("refresh_token") String refreshToken) {
        log.info("Received refresh token request");
        TokenResponse tokenResponse = keycloakService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
} 