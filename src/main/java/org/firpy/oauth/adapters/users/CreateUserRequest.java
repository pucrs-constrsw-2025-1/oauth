package org.firpy.oauth.adapters.users;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public record CreateUserRequest
        (
                String email,
                String firstName,
                String lastName,
                String password
        )
{
    UserRepresentation toKeycloakUserRepresentation()
    {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setEnabled(true);
        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(password);
        passwordCredential.setTemporary(false);
        newUser.setCredentials(List.of(passwordCredential));

        return newUser;
    }

    CreateUserResponse toResponse(String id)
    {
        return new CreateUserResponse(
                email,
                firstName,
                lastName,
                password,
                id
        );
    }
}
