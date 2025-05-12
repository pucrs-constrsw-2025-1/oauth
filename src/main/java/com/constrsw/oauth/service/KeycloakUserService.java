package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.PasswordUpdateDTO;
import com.constrsw.oauth.dto.UserDTO;
import com.constrsw.oauth.exception.KeycloakOperationException;
import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakUserService {

    @Value("${keycloak.server.url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}")
    private String keycloakClientId;

    private String getAccessTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getTokenValue();
        }
        throw new IllegalStateException("Access token could not be retrieved from security context.");
    }

    private Keycloak getKeycloakClientWithUserToken(String accessToken) {
        try {
            return KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm(realm)
                    .clientId(keycloakClientId)
                    .authorization("Bearer " + accessToken)
                    .build();
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to create Keycloak client.", e);
        }
    }

    private UserDTO mapToUserDTO(UserRepresentation userRep) {
        UserDTO dto = new UserDTO();
        dto.setId(userRep.getId());
        dto.setUsername(userRep.getUsername());
        dto.setEmail(userRep.getEmail());
        dto.setFirstName(userRep.getFirstName());
        dto.setLastName(userRep.getLastName());
        dto.setEnabled(userRep.isEnabled());
        return dto;
    }

    public UserDTO createUser(UserDTO userDto) {
        String accessToken = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);

        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(userDto.getUsername());
        userRep.setEmail(userDto.getEmail());
        userRep.setFirstName(userDto.getFirstName());
        userRep.setLastName(userDto.getLastName());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setTemporary(false);
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(userDto.getPassword());
            userRep.setCredentials(Collections.singletonList(cred));
        }

        try (Response response = keycloak.realm(realm).users().create(userRep)) {
            if (response.getStatus() == 201) {
                String createdId = CreatedResponseUtil.getCreatedId(response);
                userDto.setId(createdId);
                return getUser(createdId);
            } else {
                String errorDetails = response.readEntity(String.class);
                throw new KeycloakOperationException("Failed to create user: " + response.getStatus() + " - " + errorDetails);
            }
        } catch (Exception e) {
            throw new KeycloakOperationException("Error while creating user.", e);
        }
    }

    public List<UserDTO> getAllUsers() {
        try {
            String accessToken = getAccessTokenFromSecurityContext();
            Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
            return keycloak.realm(realm).users().list()
                    .stream()
                    .map(this::mapToUserDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to retrieve users.", e);
        }
    }

    public UserDTO getUser(String id) {
        try {
            String accessToken = getAccessTokenFromSecurityContext();
            Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
            UserRepresentation userRep = keycloak.realm(realm).users().get(id).toRepresentation();
            return mapToUserDTO(userRep);
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to retrieve user with ID: " + id, e);
        }
    }

    public void updateUser(String id, UserDTO userDto) {
        try {
            String accessToken = getAccessTokenFromSecurityContext();
            Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
            UserResource userResource = keycloak.realm(realm).users().get(id);
            UserRepresentation userRep = userResource.toRepresentation();

            if (userDto.getEmail() != null) userRep.setEmail(userDto.getEmail());
            if (userDto.getFirstName() != null) userRep.setFirstName(userDto.getFirstName());
            if (userDto.getLastName() != null) userRep.setLastName(userDto.getLastName());
            if (userDto.getEnabled() != null) userRep.setEnabled(userDto.getEnabled());

            userResource.update(userRep);
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to update user with ID: " + id, e);
        }
    }

    public void updatePassword(String id, PasswordUpdateDTO passwordDto) {
        try {
            String accessToken = getAccessTokenFromSecurityContext();
            Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
            UserResource userResource = keycloak.realm(realm).users().get(id);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(passwordDto.getPassword());

            userResource.resetPassword(passwordCred);
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to update password for user with ID: " + id, e);
        }
    }

    public void disableUser(String id) {
        try {
            String accessToken = getAccessTokenFromSecurityContext();
            Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
            UserResource userResource = keycloak.realm(realm).users().get(id);
            UserRepresentation userRep = userResource.toRepresentation();
            userRep.setEnabled(false);
            userResource.update(userRep);
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to disable user with ID: " + id, e);
        }
    }

    public void addRealmRoleToUser(String userId, String roleName) {
        try {
            String accessToken = getAccessTokenFromSecurityContext();
            Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
            RealmResource realmResource = keycloak.realm(realm);
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
            UserResource userResource = realmResource.users().get(userId);
            userResource.roles().realmLevel().add(List.of(role));
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to add role '" + roleName + "' to user with ID: " + userId, e);
        }
    }

    public void removeRealmRoleFromUser(String userId, String roleName) {
        try {
            String accessToken = getAccessTokenFromSecurityContext();
            Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
            RealmResource realmResource = keycloak.realm(realm);
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
            UserResource userResource = realmResource.users().get(userId);
            userResource.roles().realmLevel().remove(List.of(role));
        } catch (Exception e) {
            throw new KeycloakOperationException("Failed to remove role '" + roleName + "' from user with ID: " + userId, e);
        }
    }
}