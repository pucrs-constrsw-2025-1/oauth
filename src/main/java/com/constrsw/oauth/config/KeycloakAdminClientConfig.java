package com.constrsw.oauth.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuração específica do cliente admin do Keycloak
 */
@Configuration
@Slf4j
public class KeycloakAdminClientConfig {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    
    @Value("${keycloak.admin.username}")
    private String adminUsername;
    
    @Value("${keycloak.admin.password}")
    private String adminPassword;
    
    @Value("${keycloak.external-host:${KEYCLOAK_EXTERNAL_HOST:localhost}}")
    private String externalHost;
    
    @Value("${keycloak.external-console-port:${KEYCLOAK_EXTERNAL_CONSOLE_PORT:8090}}")
    private String externalConsolePort;

    /**
     * Cria o bean Keycloak para administração
     * @return Cliente Keycloak configurado
     */
    @Bean
    @Primary
    public Keycloak keycloakAdminClient() {
        log.info("Criando cliente Keycloak Admin com serverUrl: {}, realm: {}, clientId: {}", 
                 determineEffectiveServerUrl(), realm, clientId);
        
        return KeycloakBuilder.builder()
                .serverUrl(determineEffectiveServerUrl())
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(adminUsername)
                .password(adminPassword)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
    
    /**
     * Determina a URL efetiva do servidor Keycloak com base no ambiente
     */
    private String determineEffectiveServerUrl() {
        String effectiveServerUrl = serverUrl;
        
        // Check if we're running outside of Docker
        boolean isLocalEnvironment = System.getProperty("local.testing") != null || 
                                    "true".equals(System.getenv("LOCAL_TESTING"));
        
        if (isLocalEnvironment) {
            effectiveServerUrl = "http://" + externalHost + ":" + externalConsolePort;
            log.info("Modo teste local ativado, usando URL Keycloak: {}", effectiveServerUrl);
        }
        
        // Remove trailing slash if present
        if (effectiveServerUrl.endsWith("/")) {
            effectiveServerUrl = effectiveServerUrl.substring(0, effectiveServerUrl.length() - 1);
        }
        
        return effectiveServerUrl;
    }
}