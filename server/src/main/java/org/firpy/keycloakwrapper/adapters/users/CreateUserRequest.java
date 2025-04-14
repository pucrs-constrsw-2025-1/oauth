package org.firpy.keycloakwrapper.adapters.users;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public record CreateUserRequest
(
	String username,
	String firstName,
	String lastName,
	String email,
	String password
)
{
	UserRepresentation toKeycloakUserRepresentation()
	{
		UserRepresentation newUser = new UserRepresentation();
		newUser.setUsername(username);
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

	CreateUserResponse toResponse(String id) {
        return new CreateUserResponse(
                username,
                firstName,
                lastName,
                email,
                password,
                id
        );
	}
}
