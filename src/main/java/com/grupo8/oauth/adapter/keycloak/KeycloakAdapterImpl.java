package com.grupo8.oauth.adapter.keycloak;

import com.grupo8.oauth.application.DTOs.GroupDTO;
import com.grupo8.oauth.application.DTOs.PermissionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KeycloakAdapterImpl implements KeycloakAdapter {

        private final String realm;

        private final String clientId;

        private final String clientSecret;

        private final WebClient webClient;

        public KeycloakAdapterImpl(
                        @Value("${keycloak.url}") String url,
                        @Value("${keycloak.realm}") String realm,
                        @Value("${keycloak.client-id}") String clientId,
                        @Value("${keycloak.client-secret}") String clientSecret) {
                this.realm = realm;
                this.clientId = clientId;
                this.clientSecret = clientSecret;
                this.webClient = WebClient.builder()
                                .baseUrl(url)
                                .build();
        }

        @Override
        public KeycloakToken authenticateUser(String username, String password) {
                return webClient.post()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/realms/{realm}/protocol/openid-connect/token")
                                                .build(realm))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .bodyValue("grant_type=password&client_id=" + clientId + "&client_secret="
                                                + clientSecret + "&username=" + username + "&password=" + password)
                                .retrieve()
                                .bodyToMono(KeycloakToken.class)
                                .block();
        }

        @Override
        public KeycloakToken refreshToken(String refreshToken) {
                return webClient.post()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/realms/{realm}/protocol/openid-connect/token")
                                                .build(realm))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .bodyValue("grant_type=refresh_token&client_id=" + clientId + "&client_secret="
                                                + clientSecret + "&refresh_token=" + refreshToken)
                                .retrieve()
                                .bodyToMono(KeycloakToken.class)
                                .block();
        }

        @Override
        public KeycloakUser createUser(String token, KeycloakUserRegistration userRegistration) {
                ResponseEntity<Void> response = webClient.post()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users")
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .header("Content-Type", "application/json")
                                .body(BodyInserters.fromValue(userRegistration))
                                .retrieve()
                                .toBodilessEntity()
                                .block();

                assert response != null;
                return new KeycloakUser(getUserId(getHeaderValue(response.getHeaders())), userRegistration.username(),
                                userRegistration.firstName(), userRegistration.lastName(), userRegistration.enabled());
        }

        private String getHeaderValue(HttpHeaders headers) {
                return headers.getFirst("Location");
        }

        private UUID getUserId(String location) {
                Matcher matcher = Pattern.compile("/users/(.*)$").matcher(location);
                if (matcher.find()) {
                        return UUID.fromString(matcher.group(1));
                }
                return null;
        }

        @Override
        public KeycloakUser getUserById(String token, UUID id) {
                return webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users/" + id)
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToMono(KeycloakUser.class)
                                .block();
        }

        public Collection<KeycloakUser> getUsers(String token) {
                return webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users")
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToFlux(KeycloakUser.class)
                                .collectList()
                                .block();
        }

        @Override
        public KeycloakUser updateUser(String token, UUID id, KeycloakUserRegistration keycloakUserRegistration) {
                ResponseEntity<Void> response = webClient.put()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users/" + id)
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .header("Content-Type", "application/json")
                                .body(BodyInserters.fromValue(keycloakUserRegistration))
                                .retrieve()
                                .toBodilessEntity()
                                .block();

                assert response != null;
                return new KeycloakUser(id, keycloakUserRegistration.username(), keycloakUserRegistration.firstName(),
                                keycloakUserRegistration.lastName(), keycloakUserRegistration.enabled());
        }

        @Override
        public void changePassword(String token, UUID userId, KeycloackCredential keycloackCredential) {
                webClient.put()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users/" + userId + "/reset-password")
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .header("Content-Type", "application/json")
                                .body(BodyInserters.fromValue(keycloackCredential))
                                .retrieve()
                                .toBodilessEntity()
                                .block();
        }

        @Override
        public void deleteUser(String token, UUID id) {
                Map<String, Object> disableUserRequestBody = Map.of("enabled", false);
                webClient.put()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users/" + id)
                                                .build(realm))
                                .headers(headers -> {
                                        headers.setBearerAuth(token);
                                })
                                .body(BodyInserters.fromValue(disableUserRequestBody))
                                .retrieve()
                                .toBodilessEntity()
                                .block();
        }

        @Override
        public Collection<GroupDTO> getGroups(String token) {
                return webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/groups")
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToFlux(GroupDTO.class)
                                .collectList()
                                .block();
        }

        @Override
        public void addUserGroup(String token, UUID userId, String group) {
                webClient.put()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users/" + userId + "/groups/" + group)
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .toBodilessEntity()
                                .block();
        }

        @Override
        public void deleteUserGroup(String token, UUID userId, String group) {
                webClient.delete()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users/" + userId + "/groups/" + group)
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .toBodilessEntity()
                                .block();
        }

        @Override
        public Collection<GroupDTO> getUserGroups(String token, UUID userId) {
                return webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/admin/realms/{realm}/users/" + userId + "/groups")
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToFlux(GroupDTO.class)
                                .collectList()
                                .block();
        }

        @Override
        public Collection<PermissionDTO> getUserPermissions(String token) {
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.put("grant_type", List.of("urn:ietf:params:oauth:grant-type:uma-ticket"));
                formData.put("audience", List.of(clientId));
                formData.put("response_mode", List.of("permissions"));

                return webClient.post()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/realms/{realm}/protocol/openid-connect/token")
                                                .build(realm))
                                .header("Authorization", "Bearer " + token)
                                .bodyValue(formData)
                                .retrieve()
                                .bodyToFlux(PermissionDTO.class)
                                .collectList()
                                .block();
        }
}
