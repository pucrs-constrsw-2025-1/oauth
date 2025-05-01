package com.grupo_4.oauth.service;

import com.grupo_4.oauth.config.KeycloakConfig;
import com.grupo_4.oauth.model.RoleResponse;
import com.grupo_4.oauth.model.RoleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;

    public List<RoleResponse> getAllRoles(String accessToken) {
        String url = String.format("http://%s:%s/admin/realms/%s/roles",
                keycloakConfig.getKeycloakInternalHost(),
                keycloakConfig.getKeycloakInternalPort(),
                keycloakConfig.getRealm());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<RoleResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RoleResponse[].class
            );
            return Arrays.asList(response.getBody());
        } catch (RestClientException ex) {
            log.error("Error fetching roles from Keycloak: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public RoleResponse getRoleByName(String accessToken, String roleName) {
        String url = String.format("http://%s:%s/admin/realms/%s/roles/%s",
                keycloakConfig.getKeycloakInternalHost(),
                keycloakConfig.getKeycloakInternalPort(),
                keycloakConfig.getRealm(),
                roleName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<RoleResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RoleResponse.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Error fetching role by name from Keycloak: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public RoleResponse getRoleById(String accessToken, String roleId) {
        String url = String.format("http://%s:%s/admin/realms/%s/roles-by-id/%s",
                keycloakConfig.getKeycloakInternalHost(),
                keycloakConfig.getKeycloakInternalPort(),
                keycloakConfig.getRealm(),
                roleId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<RoleResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RoleResponse.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Error fetching role by id from Keycloak: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public void createRole(String accessToken, RoleRequest request) {
        String url = String.format("http://%s:%s/admin/realms/%s/roles",
                keycloakConfig.getKeycloakInternalHost(),
                keycloakConfig.getKeycloakInternalPort(),
                keycloakConfig.getRealm());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        HttpEntity<RoleRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (RestClientException ex) {
            log.error("Error creating role in Keycloak: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public void updateRole(String accessToken, String roleId, RoleRequest request) {
        // Busca o role para pegar o nome atual
        RoleResponse existing = getRoleById(accessToken, roleId);
        String url = String.format("http://%s:%s/admin/realms/%s/roles/%s",
                keycloakConfig.getKeycloakInternalHost(),
                keycloakConfig.getKeycloakInternalPort(),
                keycloakConfig.getRealm(),
                existing.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        HttpEntity<RoleRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
        } catch (RestClientException ex) {
            log.error("Error updating role in Keycloak: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public void patchRole(String accessToken, String roleId, Map<String, Object> updates) {
        // Busca o role atual
        RoleResponse existing = getRoleById(accessToken, roleId);
        RoleRequest merged = new RoleRequest();
        merged.setName(existing.getName());
        merged.setDescription(existing.getDescription());
        merged.setComposite(existing.isComposite());
        merged.setClientRole(existing.isClientRole());
        merged.setContainerId(existing.getContainerId());
        // Aplica apenas os campos enviados
        updates.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(RoleRequest.class, k);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, merged, v);
            }
        });
        updateRole(accessToken, roleId, merged);
    }

    public void deleteRole(String accessToken, String roleId) {
        // Exclusão lógica: renomear o role (ex: adicionar _deleted) e/ou marcar description
        RoleResponse existing = getRoleById(accessToken, roleId);
        RoleRequest update = new RoleRequest();
        update.setName(existing.getName() + "_deleted" + System.currentTimeMillis());
        update.setDescription("[DELETED] " + (existing.getDescription() != null ? existing.getDescription() : ""));
        update.setComposite(existing.isComposite());
        update.setClientRole(existing.isClientRole());
        update.setContainerId(existing.getContainerId());
        updateRole(accessToken, roleId, update);
    }

    public void assignRoleToUser(String accessToken, String userId, String roleId) {
        RoleResponse role = getRoleById(accessToken, roleId);
        String url = String.format("http://%s:%s/admin/realms/%s/users/%s/role-mappings/realm",
                keycloakConfig.getKeycloakInternalHost(),
                keycloakConfig.getKeycloakInternalPort(),
                keycloakConfig.getRealm(),
                userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(List.of(role), headers);
        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (RestClientException ex) {
            log.error("Error assigning role to user in Keycloak: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public void removeRoleFromUser(String accessToken, String userId, String roleId) {
        RoleResponse role = getRoleById(accessToken, roleId);
        String url = String.format("http://%s:%s/admin/realms/%s/users/%s/role-mappings/realm",
                keycloakConfig.getKeycloakInternalHost(),
                keycloakConfig.getKeycloakInternalPort(),
                keycloakConfig.getRealm(),
                userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(List.of(role), headers);
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (RestClientException ex) {
            log.error("Error removing role from user in Keycloak: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
} 