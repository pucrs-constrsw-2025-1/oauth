package com.grupo1.oauth.service;

import com.grupo1.oauth.dto.LoginRequest;
import com.grupo1.oauth.dto.TokenResponse;
import com.grupo1.oauth.dto.UserRequest;
import com.grupo1.oauth.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final WebClient webClient;

    public TokenResponse login(LoginRequest loginRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", loginRequest.getUsername());
        formData.add("password", loginRequest.getPassword());

        return webClient.post()
                .uri(keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    public void createUser(UserRequest user, String token) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", user.getUsername());
        payload.put("email", user.getUsername()); // email == username
        payload.put("emailVerified", true);
        payload.put("enabled", true);
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("credentials", List.of(Map.of(
                "type", "password",
                "value", user.getPassword(),
                "temporary", false
        )));

        webClient.post()
            .uri(keycloakUrl + "/admin/realms/" + realm + "/users")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve();
    }

    public List<UserResponse> getUsers(String token, Optional<Boolean> enabled) {

        List<Map<String, Object>> response = webClient.get()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        assert response != null;

        return response.stream()
                .map(map -> {
                    UserResponse u = new UserResponse();
                    u.setId((String) map.get("id"));
                    u.setUsername((String) map.get("username"));
                    u.setFirstName((String) map.get("firstName"));
                    u.setLastName((String) map.get("lastName"));
                    u.setEnabled((Boolean) map.getOrDefault("enabled", true));
                    return u;
                })
                .filter(user -> enabled.map(e -> user.isEnabled() == e).orElse(true))
                .toList();
    }

    public UserResponse getUserById(String token, String id) {
        Map<String, Object> response = webClient.get()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        UserResponse user = new UserResponse();
        user.setId(id);
        assert response != null;
        user.setUsername((String) response.get("username"));
        user.setFirstName((String) response.get("firstName"));
        user.setLastName((String) response.get("lastName"));
        user.setEnabled((Boolean) response.getOrDefault("enabled", true));

        return user;
    }

    public void updateUser(String token, String id, UserRequest user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("email", user.getUsername()); // email = username
        payload.put("enabled", true);
        payload.put("emailVerified", true);

        webClient.put()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void updatePassword(String token, String id, String password) {
        Map<String, String> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", "false");

        webClient.put()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/" + id + "/reset-password")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(credential)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void disableUser(String token, String id) {
        Map<String, Object> payload = Map.of("enabled", false);

        webClient.put()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

