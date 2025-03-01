package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import org.firpy.keycloakwrapper.adapters.users.CreateUserRequest;

public record KeycloakUser(
		String username, String firstName, String lastName, String email, String password
) {
	public CreateUserRequest toRequest() {
		return new CreateUserRequest(username, firstName, lastName, email, password);
	}
}
