package com.grupo1.oauth.service;

import com.grupo1.oauth.dto.RoleRequest;
import com.grupo1.oauth.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final WebClient webClient;
    private final String keycloakUrl = "http://localhost:8090/admin/realms/constrsw";

    public void createRole(String token, RoleRequest roleRequest) {
        webClient.post()
                .uri(keycloakUrl + "/roles")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleRequest)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<RoleResponse> getAllRoles(String token) {
        return webClient.get()
                .uri(keycloakUrl + "/roles")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RoleResponse>>() {})
                .block();
    }

    public RoleResponse getRoleById(String token, String id) {
        return webClient.get()
                .uri(keycloakUrl + "/roles/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RoleResponse.class)
                .block();
    }

    public void updateRole(String token, String id, RoleRequest roleRequest) {
        webClient.put()
                .uri(keycloakUrl + "/roles/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleRequest)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void patchRole(String token, String id, Map<String, Object> updates) {
        webClient.patch()
                .uri(keycloakUrl + "/roles/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updates)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void deleteRole(String token, String id) {
        webClient.delete()
                .uri(keycloakUrl + "/roles/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void assignRoleToUser(String token, String roleId, String userId) {
        webClient.post()
                .uri(keycloakUrl + "/users/{userId}/role-mappings/realm", userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("id", roleId)))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void removeRoleFromUser(String token, String roleId, String userId) {
        webClient.delete()
                .uri(keycloakUrl + "/users/{userId}/role-mappings/realm/{roleId}", userId, roleId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
