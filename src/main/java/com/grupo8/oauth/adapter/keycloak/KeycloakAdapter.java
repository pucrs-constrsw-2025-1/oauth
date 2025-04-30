package com.grupo8.oauth.adapter.keycloak;

import com.grupo8.oauth.application.DTOs.GroupDTO;
import com.grupo8.oauth.application.DTOs.PermissionDTO;

import java.util.Collection;
import java.util.UUID;

public interface KeycloakAdapter {

    KeycloakToken authenticateUser(String username, String password);

    KeycloakToken refreshToken(String refreshToken);

    KeycloakUser createUser(String authorizationHeader, KeycloakUserRegistration user);

    KeycloakUser getUserById(String token, UUID id);

    Collection<KeycloakUser> getUsers(String token);

    KeycloakUser updateUser(String token, UUID id, KeycloakUserRegistration keycloakUserRegistration);

    void changePassword(String token, UUID userId, KeycloackCredential keycloackCredential);

    void deleteUser(String token, UUID id);

    void addUserGroup(String token, UUID userId, String group);

    void deleteUserGroup(String token, UUID userId, String group);

    Collection<GroupDTO> getGroups(String token);

    Collection<GroupDTO> getUserGroups(String token, UUID userId);

    Collection<PermissionDTO> getUserPermissions(String token);
}
