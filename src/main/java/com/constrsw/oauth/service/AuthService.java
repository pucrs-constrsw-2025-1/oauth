package com.constrsw.oauth.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${keycloak.server.url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    public AuthResponse authenticate(AuthRequest authRequest) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(authRequest.getUsername())
                .password(authRequest.getPassword())
                .build();

        var accessToken = keycloak.tokenManager().getAccessToken();

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken.getToken())
                .expiresIn((int) accessToken.getExpiresIn())
                .refreshToken(accessToken.getRefreshToken())
                .refreshExpiresIn((int) accessToken.getRefreshExpiresIn())
                .build();
    }
}