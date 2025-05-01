package com.constrsw.oauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/auth/debug")
public class DebugController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @PostMapping("/token")
    public ResponseEntity<?> testToken(@RequestParam String username, 
                                       @RequestParam String password) {
        try {
            String keycloakUrl = "http://keycloak:8080/realms/constrsw/protocol/openid-connect/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", "oauth");
            map.add("client_secret", "wsNXUxaupU9X6jCncsn3rOEy6PDt7oJO");
            map.add("grant_type", "password");
            map.add("username", username);
            map.add("password", password);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                keycloakUrl, request, String.class);
            
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro: " + e.getMessage());
        }
    }
}