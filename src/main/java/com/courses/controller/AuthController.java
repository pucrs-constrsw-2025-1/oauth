package com.courses.controller;

import com.courses.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Value("${keycloak.token-uri}")
    private String keycloakTokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @PostMapping("/login")
    public ResponseEntity<?> login(@ModelAttribute LoginRequest loginRequest) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            Map<String, String> params = new LinkedHashMap<>();
            params.put("grant_type", "password");
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("username", loginRequest.getUsername());
            params.put("password", loginRequest.getPassword());

            StringBuilder formData = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            HttpEntity<String> entity = new HttpEntity<>(formData.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(keycloakTokenUri, entity, Map.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos");
        }
    }
}
