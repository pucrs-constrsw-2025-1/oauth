package org.firpy.oauth.adapters.users;

import org.keycloak.representations.idm.UserRepresentation;

public record UpdateUserRequest
(
	String firstName,
	String lastName
)
{
	UserRepresentation toKeycloakUserRepresentation()
	{
		UserRepresentation newUser = new UserRepresentation();
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);

		return newUser;
	}
}
