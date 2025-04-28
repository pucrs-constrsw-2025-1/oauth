package com.grupo_4.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.grupo_4.oauth.config.KeycloakConfig;
import com.grupo_4.oauth.exception.AuthenticationException;
import com.grupo_4.oauth.exception.TokenRefreshException;
import com.grupo_4.oauth.model.LoginRequest;
import com.grupo_4.oauth.model.TokenResponse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;
    
    public TokenResponse authenticate(LoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", loginRequest.getClient_id());
        map.add("client_secret", keycloakConfig.getClientSecret());
        map.add("username", loginRequest.getUsername());
        map.add("password", loginRequest.getPassword());
        map.add("grant_type", loginRequest.getGrant_type());
        
        logger.info("Authentication request details:");
        logger.info("client_id: {}", loginRequest.getClient_id());
        logger.info("client_secret: [MASKED]");
        logger.info("username: {}", loginRequest.getUsername());
        logger.info("password: [MASKED]");
        logger.info("grant_type: {}", loginRequest.getGrant_type());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        
        String tokenUrl = keycloakConfig.getTokenUrl();
        logger.info("Attempting to connect to Keycloak at: {}", tokenUrl);
        
        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    request,
                    TokenResponse.class);
            
            logger.info("Successfully received token response");
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized ex) {
            logger.error("Authentication failed: {}", ex.getMessage(), ex);
            throw new AuthenticationException("Invalid credentials", ex);
        } catch (HttpClientErrorException ex) {
            logger.error("HTTP client error during authentication: {}", ex.getMessage(), ex);
            throw new AuthenticationException("Authentication failed: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            logger.error("Error connecting to Keycloak: {}", ex.getMessage(), ex);
            throw new AuthenticationException("Unable to connect to authentication service", ex);
        }
    }
    
    public TokenResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenRefreshException("Refresh token cannot be null or empty");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", keycloakConfig.getClientId());
        map.add("client_secret", keycloakConfig.getClientSecret());
        map.add("refresh_token", refreshToken);
        map.add("grant_type", "refresh_token");
        
        logger.info("Refresh token request details:");
        logger.info("client_id: {}", keycloakConfig.getClientId());
        logger.info("client_secret: [MASKED]");
        logger.info("refresh_token: [FIRST 10 CHARS] {}", 
                refreshToken.length() > 10 ? refreshToken.substring(0, 10) + "..." : "[INVALID TOKEN]");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        
        String tokenUrl = keycloakConfig.getTokenUrl();
        logger.info("Attempting to refresh token at: {}", tokenUrl);
        
        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    request,
                    TokenResponse.class);
            
            logger.info("Successfully refreshed token");
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized ex) {
            logger.error("Token refresh unauthorized: {}", ex.getMessage(), ex);
            throw new TokenRefreshException("Invalid or expired refresh token", ex);
        } catch (HttpClientErrorException ex) {
            logger.error("HTTP client error during token refresh: {}", ex.getMessage(), ex);
            throw new TokenRefreshException("Token refresh failed: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            logger.error("Error refreshing token: {}", ex.getMessage(), ex);
            throw new TokenRefreshException("Unable to connect to authentication service", ex);
        }
    }
} 