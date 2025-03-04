package org.firpy.keycloakwrapper.adapters.clients;

public record ClientRepresentation
(
	String id,
	String clientId,
	String name,
	String description,
	String type,
	Boolean enabled,
	String clientAuthenticatorType,
	String secret,
	String origin
)
{}