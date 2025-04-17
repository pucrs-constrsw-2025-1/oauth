package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.UserRequest;
import com.constrsw.oauth.dto.UserResponse;
import com.constrsw.oauth.exception.CustomExceptionHandler;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public UserResponse createUser(UserRequest userRequest) {
        UsersResource usersResource = getUsersResource();

        // Check if user already exists
        List<UserRepresentation> existingUsers = usersResource.search(userRequest.getUsername(), true);
        if (!existingUsers.isEmpty()) {
            throw new CustomExceptionHandler();
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        // Create user
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new CustomExceptionHandler();
        }

        // Get user ID from location header
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRequest.getPassword());
        credential.setTemporary(false);

        UserResource userResource = usersResource.get(userId);
        userResource.resetPassword(credential);

        return mapToUserResponse(userResource.toRepresentation());
    }

    public List<UserResponse> getAllUsers(Boolean enabled) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> users = enabled != null ? 
            usersResource.search(null, null, null, null, enabled, null, null, enabled, enabled) : 
            usersResource.list();

        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        try {
            UserResource userResource = getUsersResource().get(id);
            return mapToUserResponse(userResource.toRepresentation());
        } catch (NotFoundException e) {
            throw new CustomExceptionHandler();
        }
    }

    public void updateUser(String id, UserRequest userRequest) {
        try {
            UserResource userResource = getUsersResource().get(id);
            UserRepresentation user = userResource.toRepresentation();
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new CustomExceptionHandler();
        }
    }

    public void updatePassword(String id, String newPassword) {
        try {
            UserResource userResource = getUsersResource().get(id);
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);
            userResource.resetPassword(credential);
        } catch (NotFoundException e) {
            throw new CustomExceptionHandler();
        }
    }

    public void disableUser(String id) {
        try {
            UserResource userResource = getUsersResource().get(id);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(false);
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new CustomExceptionHandler();
        }
    }

    private UsersResource getUsersResource() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.users();
    }

    private UserResponse mapToUserResponse(UserRepresentation user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.isEnabled())
                .build();
    }
}