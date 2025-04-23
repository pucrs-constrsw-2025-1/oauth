package com.grupo8.oauth.service;

import com.grupo8.oauth.dto.LoginRequest;
import com.grupo8.oauth.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    @Value("${KEYCLOAK_INTERNAL_HOST}")
    private String keycloakHost;

    @Value("${KEYCLOAK_INTERNAL_PORT}")
    private String keycloakPort;

    @Value("${KEYCLOAK_REALM}")
    private String keycloakRealm;

    @Value("${KEYCLOAK_GRANT_TYPE}")
    private String grantType;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String clientSecret;

    public String getTokenUri() {
        return "http://" + keycloakHost + ":" + keycloakPort +
                "/realms/" + keycloakRealm +
                "/protocol/openid-connect/token";
    }

    public LoginResponse login(LoginRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", grantType);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", request.getUsername());
        form.add("password", request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                getTokenUri(),
                HttpMethod.POST,
                entity,
                LoginResponse.class);

        return response.getBody();
    }
}
