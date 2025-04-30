package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import com.constrsw.oauth.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource.client-id}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.grant-type:password}")
    private String grantType;

    public AuthResponse authenticate(AuthRequest authRequest) {
        log.info("Autenticando usuário: {}", authRequest.getUsername());

        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(authRequest.getUsername())
                    .password(authRequest.getPassword())
                    .grantType(grantType)
                    .build();

            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();

            return AuthResponse.builder()
                    .tokenType(tokenResponse.getTokenType())
                    .accessToken(tokenResponse.getToken())
                    .expiresIn(tokenResponse.getExpiresIn() > 0 ? (int) tokenResponse.getExpiresIn() : null)
                    .refreshToken(tokenResponse.getRefreshToken())
                    .refreshExpiresIn(tokenResponse.getRefreshExpiresIn() > 0 ? (int) tokenResponse.getRefreshExpiresIn() : null)
                    .build();
        } catch (Exception e) {
            log.error("Erro na autenticação para usuário {}: {}", authRequest.getUsername(), e.getMessage());
            throw new GlobalException(
                    "AUTH_ERROR",
                    "Credenciais inválidas ou serviço de autenticação indisponível",
                    "AuthService",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }
}