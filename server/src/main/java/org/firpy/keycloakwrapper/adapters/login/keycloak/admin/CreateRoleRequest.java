package org.firpy.keycloakwrapper.adapters.login.keycloak.admin;

import org.keycloak.representations.idm.RoleRepresentation;

public record CreateRoleRequest
(
	String name,
	String description
)
{
	public RoleRepresentation toRoleRepresentation()
	{
		RoleRepresentation role = new RoleRepresentation();
		role.setName(name);
		role.setDescription(description);

		return role;
	}
}
