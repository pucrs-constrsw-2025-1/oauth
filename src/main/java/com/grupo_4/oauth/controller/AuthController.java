package com.grupo_4.oauth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo_4.oauth.config.KeycloakConfig;
import com.grupo_4.oauth.model.LoginRequest;
import com.grupo_4.oauth.model.TokenResponse;
import com.grupo_4.oauth.model.TokenValidationResponse;
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
    
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Received token validation request");
        try {
            String accessToken = extractTokenFromHeader(authHeader);
            log.debug("Extracted token: {}", accessToken.substring(0, Math.min(10, accessToken.length())) + "...");
            
            TokenValidationResponse validationResponse = keycloakService.validateToken(accessToken);
            
            if (validationResponse != null) {
                log.info("Token validation result: active={}, expires={}", 
                    validationResponse.isActive(),
                    validationResponse.getExpirationTime() > 0 ? 
                        new java.util.Date(validationResponse.getExpirationTime() * 1000).toString() : "N/A");
                
                // Se token não estiver ativo, retornar erro 401
                if (!validationResponse.isActive()) {
                    log.warn("Token is not active, returning 401 Unauthorized");
                    return ResponseEntity.status(401).body(validationResponse);
                }
                
                // Verificar se o token está expirado
                if (validationResponse.getExpirationTime() > 0) {
                    long expTime = validationResponse.getExpirationTime() * 1000; // convert to milliseconds
                    long currentTime = System.currentTimeMillis();
                    
                    if (currentTime > expTime) {
                        log.warn("Token is expired! Current time: {}, Expiration time: {}", 
                            new java.util.Date(currentTime), new java.util.Date(expTime));
                        validationResponse.setActive(false);
                        return ResponseEntity.status(401).body(validationResponse);
                    }
                }
            } else {
                log.warn("Token validation returned null response");
                return ResponseEntity.status(401).build();
            }
            
            return ResponseEntity.ok(validationResponse);
        } catch (Exception e) {
            log.error("Error during token validation: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
} 