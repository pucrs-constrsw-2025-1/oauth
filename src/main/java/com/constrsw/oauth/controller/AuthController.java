package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import com.constrsw.oauth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para endpoints de autenticação
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para login via parâmetros
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestParam String username,
            @RequestParam String password) {
        
        log.info("Recebida requisição de login para usuário: {}", username);
        
        AuthRequest authRequest = AuthRequest.builder()
                .username(username)
                .password(password)
                .build();
        
        AuthResponse response = authService.authenticate(authRequest);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para verificar a configuração do Keycloak
     */
    @GetMapping("/config")
    public ResponseEntity<String> getConfig() {
        return ResponseEntity.ok(authService.getConfig());
    }
}