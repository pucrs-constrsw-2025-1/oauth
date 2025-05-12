package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.RoleDTO;
import com.constrsw.oauth.exception.KeycloakOperationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakRoleService {

    @Value("${keycloak.server.url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String clientId;

    private String getAccessTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        throw new IllegalStateException("Access token could not be retrieved.");
    }

    private Keycloak getKeycloakClientWithUserToken(String accessToken) {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(realm)
                .clientId(clientId)
                .authorization("Bearer " + accessToken)
                .build();
    }

    public RoleDTO createRole(RoleDTO roleDto) {
        String token = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(token);
        RealmResource realmResource = keycloak.realm(realm);

        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        role.setComposite(false);

        realmResource.roles().create(role);
        return roleDto;
    }

    public List<RoleDTO> getAllRoles() {
        Keycloak keycloak = getKeycloakClientWithUserToken(getAccessTokenFromSecurityContext());
        return keycloak.realm(realm).roles().list().stream()
                .map(r -> new RoleDTO(r.getName(), r.getDescription()))
                .collect(Collectors.toList());
    }

    public RoleDTO getRole(String name) {
        Keycloak keycloak = getKeycloakClientWithUserToken(getAccessTokenFromSecurityContext());
        RoleRepresentation role = keycloak.realm(realm).roles().get(name).toRepresentation();
        return new RoleDTO(role.getName(), role.getDescription());
    }

    public void updateRole(String name, RoleDTO dto) {
        Keycloak keycloak = getKeycloakClientWithUserToken(getAccessTokenFromSecurityContext());
        RoleRepresentation role = new RoleRepresentation();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        keycloak.realm(realm).roles().get(name).update(role);
    }

    public void patchRole(String name, RoleDTO dto) {
        Keycloak keycloak = getKeycloakClientWithUserToken(getAccessTokenFromSecurityContext());
        RoleRepresentation role = keycloak.realm(realm).roles().get(name).toRepresentation();
        if (dto.getDescription() != null) role.setDescription(dto.getDescription());
        keycloak.realm(realm).roles().get(name).update(role);
    }

    public void disableRole(String name) {
        // Keycloak does not support logical deletion for roles directly.
        // This is a workaround: prepend a marker to name or set a disabled description.
        patchRole(name, new RoleDTO(null, "[DISABLED]"));
    }
}
