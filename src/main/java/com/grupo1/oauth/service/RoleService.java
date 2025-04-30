package com.grupo1.oauth.service;

import com.grupo1.oauth.dto.RoleDetailResponse;
import com.grupo1.oauth.dto.RoleRequest;
import com.grupo1.oauth.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final WebClient webClient;

    @Value("${keycloak.server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    public void createRole(String token, RoleRequest roleRequest) {
        webClient.post()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleRequest)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<RoleResponse> getAllRoles(String token) {
        return webClient.get()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RoleResponse>>() {})
                .block();
    }

    public RoleResponse getRoleById(String token, String id) {
        return webClient.get()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles-by-id/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RoleResponse.class)
                .block();
    }

    public void updateRole(String token, String id, RoleRequest roleRequest) {
        webClient.put()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles-by-id/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleRequest)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void patchRole(String token, String id, Map<String, Object> updates) {
        RoleResponse existingRole = getRoleById(token, id);

        // Map RoleResponse to RoleRequest
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName(existingRole.getName());
        roleRequest.setDescription(existingRole.getDescription());

        updates.forEach((key, value) -> {
            try {
                var field = roleRequest.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(roleRequest, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to update field: " + key, e);
            }
        });

        // Use the put method to update the role
        webClient.put()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles-by-id/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleRequest)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void deleteRole(String token, String id) {
        webClient.delete()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles-by-id/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void assignRoleToUser(String token, String roleId, String userId) {
        RoleDetailResponse role = webClient.get()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles-by-id/" + roleId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RoleDetailResponse.class)
                .block();

        if (role == null) {
            throw new RuntimeException("Role not found: " + roleId);
        }

        webClient.post()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/"+ userId + "/role-mappings/realm")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(role))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void removeRoleFromUser(String token, String roleId, String userId) {
        RoleDetailResponse role = webClient.get()
                .uri(keycloakUrl + "/admin/realms/" + realm + "/roles-by-id/" + roleId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RoleDetailResponse.class)
                .block();

        if (role == null) {
            throw new RuntimeException("Role not found: " + roleId);
        }

        webClient.method(HttpMethod.DELETE)
                .uri(keycloakUrl + "/admin/realms/" + realm + "/users/"+ userId + "/role-mappings/realm")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(role))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
