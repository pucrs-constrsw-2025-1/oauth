package com.constrsw.oauth.service;

import com.constrsw.oauth.config.KeycloakConfig;
import com.constrsw.oauth.dto.AuthRequest;
import com.constrsw.oauth.dto.AuthResponse;
import com.constrsw.oauth.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Implementação do serviço Keycloak que consome diretamente a API REST
 */
@Service
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakConfig config;
    private final RestTemplate restTemplate;

    public KeycloakServiceImpl(KeycloakConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        log.info("Autenticando usuário: {}", authRequest.getUsername());

        try {
            // Determine the environment and adjust the Keycloak URL
            String effectiveServerUrl = determineEffectiveServerUrl();
            
            // Construct token endpoint URL
            String tokenUrl = buildTokenEndpointUrl(effectiveServerUrl);
            
            // Prepare request headers and body
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_id", config.getClientId());
            formData.add("client_secret", config.getClientSecret());
            formData.add("grant_type", config.getGrantType());
            formData.add("username", authRequest.getUsername());
            formData.add("password", authRequest.getPassword());
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            
            // Execute REST call to Keycloak
            log.debug("Enviando requisição de token para: {}", tokenUrl);
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    tokenUrl, request, AuthResponse.class);
            
            log.info("Token obtido com sucesso para usuário: {}", authRequest.getUsername());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Erro na autenticação para usuário {}: {}", authRequest.getUsername(), e.getMessage(), e);
            throw new GlobalException(
                    "AUTH_ERROR",
                    "Credenciais inválidas ou serviço de autenticação indisponível: " + e.getMessage(),
                    "KeycloakService",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }
    
    @Override
    public String getConfigInfo() {
        return "Keycloak URL: " + config.getServerUrl() + "\n" +
               "Realm: " + config.getRealm() + "\n" +
               "Client ID: " + config.getClientId();
    }
    
    /**
     * Determina a URL efetiva do servidor Keycloak com base no ambiente
     */
    private String determineEffectiveServerUrl() {
        String effectiveServerUrl = config.getServerUrl();
        
        // Check if we're running outside of Docker and adjust the URL
        boolean isLocalEnvironment = System.getProperty("local.testing") != null || 
                                    "true".equals(System.getenv("LOCAL_TESTING"));
        
        if (isLocalEnvironment) {
            effectiveServerUrl = "http://" + config.getExternalHost() + ":" + 
                                config.getExternalConsolePort();
            log.info("Modo teste local ativado, usando URL Keycloak: {}", effectiveServerUrl);
        }
        
        // Remove trailing slash if present
        if (effectiveServerUrl.endsWith("/")) {
            effectiveServerUrl = effectiveServerUrl.substring(0, effectiveServerUrl.length() - 1);
        }
        
        return effectiveServerUrl;
    }
    
    /**
     * Constrói a URL do endpoint de token do Keycloak
     */
    private String buildTokenEndpointUrl(String serverUrl) {
        return serverUrl + "/realms/" + config.getRealm() + "/protocol/openid-connect/token";
    }
}