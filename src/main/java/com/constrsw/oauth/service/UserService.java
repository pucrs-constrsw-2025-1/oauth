package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.UserRequest;
import com.constrsw.oauth.dto.UserResponse;
import com.constrsw.oauth.exception.GlobalException;
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
import org.springframework.http.HttpStatus;
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

        try {
            // Check if user exists
            List<UserRepresentation> existingUsers = usersResource.search(userRequest.getUsername(), true);
            if (!existingUsers.isEmpty()) {
                throw new GlobalException(
                    "USER_EXISTS",
                    "Username already exists",
                    "UserService",
                    HttpStatus.CONFLICT
                );
            }

            // Create user
            UserRepresentation user = new UserRepresentation();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getUsername());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(true);

            Response response = usersResource.create(user);
            if (response.getStatus() != 201) {
                throw new GlobalException(
                    "CREATE_USER_FAILED",
                    "Failed to create user",
                    "UserService",
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(userRequest.getPassword());
            credential.setTemporary(false);

            usersResource.get(userId).resetPassword(credential);

            return getUserById(userId);
        } catch (Exception e) {
            throw new GlobalException(
                "USER_CREATION_ERROR",
                "Error creating user: " + e.getMessage(),
                "UserService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public List<UserResponse> getAllUsers(Boolean enabled) {
        try {
            UsersResource usersResource = getUsersResource();
            List<UserRepresentation> users = enabled != null ? 
                usersResource.search(null, null, null, null, enabled, null, null, enabled, enabled) : 
                usersResource.list();

            return users.stream()
                    .map(this::mapToUserResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new GlobalException(
                "GET_USERS_ERROR",
                "Error fetching users: " + e.getMessage(),
                "UserService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public UserResponse getUserById(String id) {
        try {
            UserRepresentation user = getUsersResource().get(id).toRepresentation();
            return mapToUserResponse(user);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "USER_NOT_FOUND",
                "User not found with id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
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
            throw new GlobalException(
                "USER_NOT_FOUND",
                "User not found with id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
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
            throw new GlobalException(
                "USER_NOT_FOUND",
                "User not found with id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
        }
    }

    public void disableUser(String id) {
        try {
            UserResource userResource = getUsersResource().get(id);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(false);
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "USER_NOT_FOUND",
                "User not found with id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
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