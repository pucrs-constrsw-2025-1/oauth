package com.constrsw.oauth.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.constrsw.oauth.dto.KeycloakTokenResponse;
import com.constrsw.oauth.dto.LoginRequest;
import com.constrsw.oauth.service.KeycloakAuthService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
            loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Utilizador e palavra-passe são obrigatórios.");
        }

        KeycloakTokenResponse tokenResponse = keycloakAuthService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
            return ResponseEntity.ok(tokenResponse);
        } else {
            // A mensagem de erro específica já foi logada no serviço
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação. Verifique as suas credenciais ou os logs do servidor.");
        }
    }
}