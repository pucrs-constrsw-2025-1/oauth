package org.firpy.keycloakwrapper.adapters.users;

public record CreateUserRequest
(
	String username,
	String firstName,
	String lastName,
	String email,
	String password
)
{
	public CreateKeycloakUserRequest toCreateKeycloakUserRequest()
	{
		return new CreateKeycloakUserRequest(username, firstName, lastName, email, password);
	}
}
