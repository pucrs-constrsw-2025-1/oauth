package com.constrsw.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.constrsw.oauth.dto.KeycloakTokenResponse;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakAuthService {

    @Value("${keycloak.server.url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public KeycloakTokenResponse login(String username, String password) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", authServerUrl, realm);
        System.out.println(tokenUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println(clientId);
        System.out.println(clientSecret);
        System.out.println(username);
        System.out.println(password);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, KeycloakTokenResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Loggar o erro ou lançar uma exceção personalizada
            System.err.println("Erro ao autenticar com o Keycloak: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            // Poderia lançar uma exceção mais específica aqui, por exemplo:
            // throw new RuntimeException("Falha na autenticação: " + e.getResponseBodyAsString(), e);
            return null; // Ou uma resposta de erro apropriada
        }
    }
}