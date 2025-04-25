package org.firpy.oauth.adapters.roles;

import org.keycloak.representations.idm.RoleRepresentation;

public record PatchRoleRequest
(
	String name,
	String description
)
{
	public void applyTo(RoleRepresentation role)
	{
		if (name != null)
		{
			role.setName(name);
		}
		if (description != null)
		{
			role.setDescription(description);
		}
	}
}
