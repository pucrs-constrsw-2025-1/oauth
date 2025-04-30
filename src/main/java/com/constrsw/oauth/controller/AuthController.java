package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import com.constrsw.oauth.service.AuthService;
import com.constrsw.oauth.exception.GlobalException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação de usuários
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints para autenticação de usuários")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Autenticar usuário", 
        description = "Autentica um usuário com as credenciais fornecidas e retorna um token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Autenticação bem-sucedida"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping(value = "/login", consumes = {
        MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Nome de usuário (e-mail)", required = true)
            @RequestParam(value = "username", required = true) String username,
            
            @Parameter(description = "Senha do usuário", required = true)
            @RequestParam(value = "password", required = true) String password) {
        
        log.info("Requisição de login para o usuário: {}", username);
        
        try {
            AuthRequest authRequest = new AuthRequest();
            authRequest.setUsername(username);
            authRequest.setPassword(password);
            
            AuthResponse response = authService.authenticate(authRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (GlobalException e) {
            log.error("Erro de autenticação: {}", e.getMessage());
            return ResponseEntity.status(e.getHttpStatus()).build();
        } catch (Exception e) {
            log.error("Erro inesperado durante autenticação: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Adicionando um método alternativo que aceita JSON para mais flexibilidade
    @PostMapping(value = "/login/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> loginJson(@RequestBody AuthRequest authRequest) {
        log.info("Requisição de login JSON para o usuário: {}", authRequest.getUsername());
        
        try {
            AuthResponse response = authService.authenticate(authRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (GlobalException e) {
            log.error("Erro de autenticação: {}", e.getMessage());
            return ResponseEntity.status(e.getHttpStatus()).build();
        } catch (Exception e) {
            log.error("Erro inesperado durante autenticação: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}