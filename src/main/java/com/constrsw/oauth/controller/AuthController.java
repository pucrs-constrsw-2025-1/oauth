package com.constrsw.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import com.constrsw.oauth.exception.ErrorResponse;
import com.constrsw.oauth.service.AuthService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Autenticar usuário", 
        description = "Autentica um usuário com as credenciais fornecidas e retorna um token JWT",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Autenticação bem-sucedida",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Exemplo de resposta de sucesso",
                        value = "{\n" +
                                "  \"token_type\": \"Bearer\",\n" +
                                "  \"access_token\": \"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZRGlOTUxuZVd...\",\n" +
                                "  \"expires_in\": 300,\n" +
                                "  \"refresh_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxMjM0NTY3...\",\n" +
                                "  \"refresh_expires_in\": 1800\n" +
                                "}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Requisição inválida",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Dados inválidos",
                        value = "{\n" +
                                "  \"error_code\": \"OA-400\",\n" +
                                "  \"error_description\": \"Requisição inválida: dados incompletos ou inválidos\",\n" +
                                "  \"error_source\": \"AuthController\",\n" +
                                "  \"error_stack\": []\n" +
                                "}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Credenciais inválidas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Credenciais inválidas",
                        value = "{\n" +
                                "  \"error_code\": \"OA-401\",\n" +
                                "  \"error_description\": \"Usuário e/ou senha inválidos\",\n" +
                                "  \"error_source\": \"AuthService\",\n" +
                                "  \"error_stack\": []\n" +
                                "}"
                    )
                }
            )
        )
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Nome de usuário (e-mail)", required = true, example = "admin@example.com")
            @RequestParam("username") String username,
            
            @Parameter(description = "Senha do usuário", required = true, example = "password")
            @RequestParam("password") String password) {
        
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword(password);
        
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}