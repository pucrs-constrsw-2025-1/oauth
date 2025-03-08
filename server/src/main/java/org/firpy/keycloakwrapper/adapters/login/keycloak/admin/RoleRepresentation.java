package org.firpy.keycloakwrapper.adapters.login.keycloak.admin;

public record RoleRepresentation
(
	String id,
	String name,
	String description,
	boolean scopeParamRequired,
	boolean composite,
	boolean clientRole,
	String containerId
)
{
}
