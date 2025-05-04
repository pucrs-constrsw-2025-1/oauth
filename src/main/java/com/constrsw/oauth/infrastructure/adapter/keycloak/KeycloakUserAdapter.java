package com.constrsw.oauth.infrastructure.adapter.keycloak;

import com.constrsw.oauth.domain.entity.User;
import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.repository.UserRepository;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class KeycloakUserAdapter implements UserRepository {
    
    private final Keycloak keycloak;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Autowired
    public KeycloakUserAdapter(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    
    @Override
    public String createUser(User user) {
        try {
            UserRepresentation userRepresentation = mapToKeycloakUser(user);
            
            // Set credentials
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(user.getPassword());
                credential.setTemporary(false);
                userRepresentation.setCredentials(Collections.singletonList(credential));
            }
            
            Response response = keycloak.realm(realm).users().create(userRepresentation);
            
            if (response.getStatus() == 201) {
                String locationHeader = response.getHeaderString("Location");
                String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
                return userId;
            } else if (response.getStatus() == 409) {
                throw new DomainException("OA-409", "User with username '" + user.getUsername() + "' already exists", "KeycloakAPI");
            } else {
                throw new DomainException("OA-" + response.getStatus(), "Failed to create user: " + response.getStatusInfo().getReasonPhrase(), "KeycloakAPI");
            }
        } catch (HttpClientErrorException e) {
            throw new DomainException("OA-" + e.getStatusCode().value(), "Failed to create user: " + e.getMessage(), "KeycloakAPI", e);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to create user: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public List<User> findAllUsers() {
        try {
            List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
            return userRepresentations.stream()
                    .map(this::mapToDomainUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to retrieve users: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public List<User> findUsersByEnabled(boolean enabled) {
        try {
            List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
            return userRepresentations.stream()
                    .filter(user -> Boolean.valueOf(enabled).equals(user.isEnabled()))
                    .map(this::mapToDomainUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to retrieve users by enabled status: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public Optional<User> findUserById(String id) {
        try {
            UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
            if (userRepresentation != null) {
                return Optional.of(mapToDomainUser(userRepresentation));
            }
            return Optional.empty();
        } catch (NotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to retrieve user by id: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void updateUser(String id, User user) {
        try {
            Optional<User> existingUser = findUserById(id);
            if (existingUser.isPresent()) {
                UserRepresentation userRepresentation = mapToKeycloakUser(user);
                userRepresentation.setId(id);
                
                // Don't update credentials in this method
                userRepresentation.setCredentials(null);
                
                keycloak.realm(realm).users().get(id).update(userRepresentation);
            } else {
                throw new DomainException("OA-404", "User not found with id: " + id, "KeycloakAPI");
            }
        } catch (HttpClientErrorException e) {
            throw new DomainException("OA-" + e.getStatusCode().value(), "Failed to update user: " + e.getMessage(), "KeycloakAPI", e);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to update user: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void updateUserPassword(String id, String password) {
        try {
            Optional<User> existingUser = findUserById(id);
            if (existingUser.isPresent()) {
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(password);
                credential.setTemporary(false);
                
                keycloak.realm(realm).users().get(id).resetPassword(credential);
            } else {
                throw new DomainException("OA-404", "User not found with id: " + id, "KeycloakAPI");
            }
        } catch (HttpClientErrorException e) {
            throw new DomainException("OA-" + e.getStatusCode().value(), "Failed to update user password: " + e.getMessage(), "KeycloakAPI", e);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to update user password: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void disableUser(String id) {
        try {
            Optional<User> existingUser = findUserById(id);
            if (existingUser.isPresent()) {
                UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
                userRepresentation.setEnabled(false);
                
                keycloak.realm(realm).users().get(id).update(userRepresentation);
            } else {
                throw new DomainException("OA-404", "User not found with id: " + id, "KeycloakAPI");
            }
        } catch (HttpClientErrorException e) {
            throw new DomainException("OA-" + e.getStatusCode().value(), "Failed to disable user: " + e.getMessage(), "KeycloakAPI", e);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to disable user: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void addRoleToUser(String userId, String roleId) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource != null) {
                RoleRepresentation role = keycloak.realm(realm).roles().get(roleId).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(role));
            } else {
                throw new DomainException("OA-404", "User not found with id: " + userId, "KeycloakAPI");
            }
        } catch (NotFoundException e) {
            throw new DomainException("OA-404", "User or role not found", "KeycloakAPI", e);
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to add role to user: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void removeRoleFromUser(String userId, String roleId) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource != null) {
                RoleRepresentation role = keycloak.realm(realm).roles().get(roleId).toRepresentation();
                userResource.roles().realmLevel().remove(Collections.singletonList(role));
            } else {
                throw new DomainException("OA-404", "User not found with id: " + userId, "KeycloakAPI");
            }
        } catch (NotFoundException e) {
            throw new DomainException("OA-404", "User or role not found", "KeycloakAPI", e);
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to remove role from user: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    private UserRepresentation mapToKeycloakUser(User user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getUsername()); // Username = Email as per requirements
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEnabled(user.isEnabled());
        userRepresentation.setEmailVerified(true);
        
        return userRepresentation;
    }
    
    private User mapToDomainUser(UserRepresentation userRepresentation) {
        User user = new User();
        user.setId(userRepresentation.getId());
        user.setUsername(userRepresentation.getUsername());
        user.setFirstName(userRepresentation.getFirstName());
        user.setLastName(userRepresentation.getLastName());
        user.setEnabled(userRepresentation.isEnabled());
        
        return user;
    }
}