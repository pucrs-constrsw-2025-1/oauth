package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import com.constrsw.oauth.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Serviço para autenticação de usuários usando Keycloak
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${keycloak.server.url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;
    
    @Value("${keycloak.grant.type}")
    private String grantType;

    /**
     * Autentica um usuário usando username e password
     *
     * @param authRequest Requisição de autenticação contendo username e password
     * @return Resposta de autenticação contendo os tokens
     * @throws GlobalException Se ocorrer um erro na autenticação
     */
    public AuthResponse authenticate(AuthRequest authRequest) {
        log.info("Autenticando usuário: {}", authRequest.getUsername());

        try {
            // Obtém o token diretamente do Keycloak
            AccessTokenResponse tokenResponse = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(authRequest.getUsername())
                    .password(authRequest.getPassword())
                    .grantType(grantType)
                    .build()
                    .tokenManager()
                    .getAccessToken();

            // Constrói a resposta
            return AuthResponse.builder()
                    .tokenType(tokenResponse.getTokenType())
                    .accessToken(tokenResponse.getToken())
                    .expiresIn(tokenResponse.getExpiresIn())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                    .build();
        } catch (Exception e) {
            log.error("Erro na autenticação: {}", e.getMessage());
            throw new GlobalException(
                    "AUTH_ERROR",
                    "Falha na autenticação: " + e.getMessage(),
                    "AuthService",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }
}