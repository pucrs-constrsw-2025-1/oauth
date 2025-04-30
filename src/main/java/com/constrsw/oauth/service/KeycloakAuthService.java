package com.constrsw.oauth.service;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.constrsw.oauth.dto.LoginRequest;
import com.constrsw.oauth.dto.LoginResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class KeycloakAuthService {

    @Value("${keycloak.server.url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        String url = serverUrl + "/auth/realms/" + realm + "/protocol/openid-connect/token";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", loginRequest.getUsername());
        body.add("password", loginRequest.getPassword());
        body.add("grant_type", loginRequest.getGrantType());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<LoginResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, LoginResponse.class);

        return response.getBody();
    }
}
