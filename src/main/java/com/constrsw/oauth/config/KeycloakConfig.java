package com.constrsw.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

/**
 * Configuração simplificada para acesso ao Keycloak
 * Apenas obtém as configurações necessárias para acessar a API REST do Keycloak
 */
@Configuration
@Getter
public class KeycloakConfig {

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
    
    @Value("${keycloak.grant-type:password}")
    private String grantType;
    
    @Value("${keycloak.external-host:${KEYCLOAK_EXTERNAL_HOST:localhost}}")
    private String externalHost;
    
    @Value("${keycloak.external-console-port:${KEYCLOAK_EXTERNAL_CONSOLE_PORT:8090}}")
    private String externalConsolePort;

    /**
     * RestTemplate para chamadas HTTP
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}