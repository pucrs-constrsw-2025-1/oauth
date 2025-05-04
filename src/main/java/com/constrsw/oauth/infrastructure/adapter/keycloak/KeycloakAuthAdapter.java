package com.constrsw.oauth.infrastructure.adapter.keycloak;

import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class KeycloakAuthAdapter implements AuthenticationService {
    
    private final RestTemplate restTemplate;
    
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    
    @Value("${keycloak.grant-type}")
    private String grantType;
    
    public KeycloakAuthAdapter() {
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public Map<String, Object> authenticate(String username, String password) {
        try {
            String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("grant_type", grantType);
            map.add("username", username);
            map.add("password", password);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new DomainException("OA-401", "Invalid username or password", "KeycloakAPI", e);
            }
            throw new DomainException("OA-500", "Authentication failed: " + e.getMessage(), "KeycloakAPI", e);
        } catch (Exception e) {
            throw new DomainException("OA-500", "Authentication failed: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("grant_type", "refresh_token");
            map.add("refresh_token", refreshToken);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new DomainException("OA-401", "Invalid refresh token", "KeycloakAPI", e);
            }
            throw new DomainException("OA-500", "Token refresh failed: " + e.getMessage(), "KeycloakAPI", e);
        } catch (Exception e) {
            throw new DomainException("OA-500", "Token refresh failed: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void logout(String refreshToken) {
        try {
            String logoutUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("refresh_token", refreshToken);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            restTemplate.exchange(
                    logoutUrl,
                    HttpMethod.POST,
                    request,
                    Void.class
            );
        } catch (Exception e) {
            throw new DomainException("OA-500", "Logout failed: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            String introspectUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token/introspect";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("token", token);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    introspectUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            
            Map<String, Object> body = response.getBody();
            return body != null && Boolean.TRUE.equals(body.get("active"));
        } catch (Exception e) {
            throw new DomainException("OA-500", "Token validation failed: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
}