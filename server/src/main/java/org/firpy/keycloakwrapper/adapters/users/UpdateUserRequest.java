package org.firpy.keycloakwrapper.adapters.users;

public record UpdateUserRequest
		(
			String firstName,
			String lastName,
			String email,
			String password
		)
{
	public UpdateKeycloakUserRequest toUpdateKeycloakUserRequest()
	{
		return new UpdateKeycloakUserRequest(firstName, lastName, email, password);
	}
}
