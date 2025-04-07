package org.firpy.keycloakwrapper.adapters.users;

import org.keycloak.representations.idm.UserRepresentation;

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

		return newUser;
	}
}
