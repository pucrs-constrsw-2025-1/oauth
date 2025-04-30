package com.constrsw.oauth.controller;

import com.constrsw.oauth.model.LoginRequest;
import com.constrsw.oauth.model.TokenResponse;
import com.constrsw.oauth.usecases.interfaces.ILoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "API para autenticação e operações relacionadas a tokens")
public class AuthController {

    private final ILoginUseCase loginUseCase;

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Realiza a autenticação do usuário com base nas credenciais fornecidas e retorna um token de acesso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas ou incompletas"),
            @ApiResponse(responseCode = "401", description = "Falha na autenticação - usuário ou senha incorretos"),
            @ApiResponse(responseCode = "403", description = "Usuário desativado ou sem permissão para acessar o sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @SecurityRequirements
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = loginUseCase.execute(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(tokenResponse);
    }
}