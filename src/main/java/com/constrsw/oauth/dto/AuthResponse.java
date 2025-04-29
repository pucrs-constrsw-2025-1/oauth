package com.constrsw.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Resposta de autenticação contendo tokens JWT")
public class AuthResponse {
    
    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZRGlOTUxuZVd...")
    private String accessToken;
    
    @Schema(description = "Tempo de expiração do token de acesso em segundos", example = "300")
    private Integer expiresIn;
    
    @Schema(description = "Token de atualização para obter um novo token de acesso", example = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxMjM0NTY3...")
    private String refreshToken;
    
    @Schema(description = "Tempo de expiração do token de atualização em segundos", example = "1800")
    private Integer refreshExpiresIn;
}