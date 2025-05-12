package com.constrsw.oauth.service;
import com.constrsw.oauth.dto.PasswordUpdateDTO;
import com.constrsw.oauth.dto.UserDTO;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
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

    @Value("${keycloak.server.url}") // Base URL of Keycloak, e.g., http://keycloak:8080
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client.id}") // Your application's client ID, e.g., oauth
    private String keycloakClientId;

    private String getAccessTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getTokenValue();
        }
        throw new IllegalStateException("Não foi possível obter o token de acesso do contexto de segurança.");
    }

private Keycloak getKeycloakClientWithUserToken(String accessToken) {
    return KeycloakBuilder.builder()
            .serverUrl(keycloakServerUrl)
            .realm(realm)
            .clientId(keycloakClientId) // Ainda útil para contexto, embora o token carregue as permissões
            // Não é necessário .grantType() quando se fornece o token de autorização diretamente
            .authorization("Bearer " + accessToken) // Forma correta de passar o token
            .build();
}

    private UserDTO mapToUserDTO(UserRepresentation userRep) {
        UserDTO dto = new UserDTO();
        dto.setId(userRep.getId());
        dto.setUsername(userRep.getUsername());
        dto.setEmail(userRep.getEmail());
        dto.setFirstName(userRep.getFirstName());
        dto.setLastName(userRep.getLastName());
        dto.setEnabled(userRep.isEnabled());
        // Map other fields as needed
        return dto;
    }

    public UserDTO createUser(UserDTO userDto) {
        String accessToken = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userDto.getUsername());
        userRepresentation.setEmail(userDto.getEmail());
        userRepresentation.setFirstName(userDto.getFirstName());
        userRepresentation.setLastName(userDto.getLastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true); // Or handle email verification flow

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userDto.getPassword());
            userRepresentation.setCredentials(Collections.singletonList(passwordCred));
        }

        UsersResource usersResource = keycloak.realm(realm).users();
        try (jakarta.ws.rs.core.Response response = usersResource.create(userRepresentation)) {
            if (response.getStatus() == 201) { // Created
                String createdId = CreatedResponseUtil.getCreatedId(response);
                userDto.setId(createdId);
                // You might want to fetch the created user to get all details populated by Keycloak
                return getUser(createdId); // Return the full DTO of the created user
            } else {
                String errorDetails = response.readEntity(String.class);
                throw new RuntimeException("Falha ao criar utilizador no Keycloak: " + response.getStatus() + " - " + errorDetails);
            }
        }
    }

    public List<UserDTO> getAllUsers() {
        String accessToken = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        return userRepresentations.stream().map(this::mapToUserDTO).collect(Collectors.toList());
    }

    public UserDTO getUser(String id) {
        String accessToken = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
        UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
        return mapToUserDTO(userRepresentation);
    }

    public void updateUser(String id, UserDTO userDto) {
        String accessToken = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
        UserResource userResource = keycloak.realm(realm).users().get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();

        // Update fields from DTO
        if (userDto.getEmail() != null) userRepresentation.setEmail(userDto.getEmail());
        if (userDto.getFirstName() != null) userRepresentation.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null) userRepresentation.setLastName(userDto.getLastName());
        if (userDto.getEnabled() != null) userRepresentation.setEnabled(userDto.getEnabled());
        // Add other updatable fields as needed

        userResource.update(userRepresentation);
    }

    public void updatePassword(String id, PasswordUpdateDTO passwordDto) {
        String accessToken = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
        UserResource userResource = keycloak.realm(realm).users().get(id);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false); // Set to true if you want user to change it on next login
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(passwordDto.getPassword());

        userResource.resetPassword(passwordCred);
    }

    public void disableUser(String id) {
        String accessToken = getAccessTokenFromSecurityContext();
        Keycloak keycloak = getKeycloakClientWithUserToken(accessToken);
        UserResource userResource = keycloak.realm(realm).users().get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(false);
        userResource.update(userRepresentation);
    }
}

// Helper class for extracting created ID (place it in a suitable utility package or as a static inner class)
class CreatedResponseUtil {
    public static String getCreatedId(jakarta.ws.rs.core.Response response) {
        String location = response.getHeaderString("Location");
        if (location == null || location.isEmpty()) {
            return null;
        }
        int lastSlash = location.lastIndexOf('/');
        if (lastSlash == -1) {
            return null;
        }
        return location.substring(lastSlash + 1);
    }
}