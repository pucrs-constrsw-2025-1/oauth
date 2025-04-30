package com.constrsw.oauth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para resposta de autenticação
 */
@Data
@Builder
public class AuthResponse {
    private String tokenType;
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private Integer refreshExpiresIn;
}