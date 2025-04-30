package com.constrsw.oauth.service;

import com.constrsw.oauth.exception.custom_exceptions.UserNotFoundException;
import com.constrsw.oauth.model.UserRequest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class KeycloakUserService {

    private final UsersResource usersResource;

    public Response createUser(UserRequest userRequest, boolean isTemporary) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(userRequest.getPassword());
            credential.setTemporary(isTemporary);

            UserRepresentation user = new UserRepresentation();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getUsername());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setCredentials(Collections.singletonList(credential));

            return usersResource.create(user);
    }

    public List<UserRepresentation> getUserByUsername(String username) {
        return usersResource.search(username, true);
    }

    public UserRepresentation getUserById(String userId) {
        try {
            return usersResource.get(userId).toRepresentation();
        } catch (NotFoundException e) {
            throw new UserNotFoundException(userId);
        }
    }

    public List<UserRepresentation> getAllUsers() {
        return usersResource.list();
    }

    public void updateUser(UserRepresentation user) {
        usersResource.get(user.getId()).update(user);
    }

    public void resetPassword(String userId, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

       usersResource.get(userId).resetPassword(credential);
    }

    public void deleteUser(String userId) {
        UserRepresentation user = usersResource.get(userId).toRepresentation();

        user.setEnabled(false);

        usersResource.get(userId).update(user);
    }
}
