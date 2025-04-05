package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import org.firpy.keycloakwrapper.adapters.users.CreateKeycloakUserRequest;

public record KeycloakUser
(
	String id,
	String username,
	String firstName,
	String lastName,
	String email
)
{
	public CreateKeycloakUserRequest toRequest(String password)
	{
		return new CreateKeycloakUserRequest(username, firstName, lastName, email, password);
	}
}
