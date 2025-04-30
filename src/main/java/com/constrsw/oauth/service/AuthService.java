package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import com.constrsw.oauth.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final Environment env;

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
            // Determine the environment and adjust the Keycloak URL if needed
            String effectiveServerUrl = serverUrl;
            
            // Check if we're running outside of Docker and adjust the URL
            boolean isLocalEnvironment = System.getProperty("local.testing") != null || 
                                        "true".equals(System.getenv("LOCAL_TESTING"));
            
            if (isLocalEnvironment) {
                effectiveServerUrl = "http://localhost:8090";
                log.info("Modo teste local ativado, usando URL Keycloak: {}", effectiveServerUrl);
            }
            
            // Remove trailing slash if present to ensure consistency
            if (effectiveServerUrl.endsWith("/")) {
                effectiveServerUrl = effectiveServerUrl.substring(0, effectiveServerUrl.length() - 1);
            }
            
            // Add "/auth" if not present
            if (!effectiveServerUrl.endsWith("/auth")) {
                effectiveServerUrl = effectiveServerUrl + "/auth";
            }
            
            log.debug("Construindo cliente Keycloak com URL: {}, Realm: {}, ClientId: {}", 
                     effectiveServerUrl, realm, clientId);
            
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(effectiveServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(authRequest.getUsername())
                    .password(authRequest.getPassword())
                    .grantType(grantType)
                    .build();

            log.debug("Solicitando token ao Keycloak");
            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            log.info("Token obtido com sucesso para usuário: {}", authRequest.getUsername());

            return AuthResponse.builder()
                    .tokenType(tokenResponse.getTokenType())
                    .accessToken(tokenResponse.getToken())
                    .expiresIn(tokenResponse.getExpiresIn() > 0 ? (int) tokenResponse.getExpiresIn() : null)
                    .refreshToken(tokenResponse.getRefreshToken())
                    .refreshExpiresIn(tokenResponse.getRefreshExpiresIn() > 0 ? (int) tokenResponse.getRefreshExpiresIn() : null)
                    .build();
        } catch (Exception e) {
            log.error("Erro na autenticação para usuário {}: {}", authRequest.getUsername(), e.getMessage(), e);
            throw new GlobalException(
                    "AUTH_ERROR",
                    "Credenciais inválidas ou serviço de autenticação indisponível: " + e.getMessage(),
                    "AuthService",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }
}