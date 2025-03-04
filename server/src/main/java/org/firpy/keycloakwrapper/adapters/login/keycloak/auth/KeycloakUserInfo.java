package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakUserInfo
(
	 String sub,
	String name,
	@JsonProperty("given_name") String givenName,
	@JsonProperty("family_name") String familyName,
	@JsonProperty("preferred_username") String preferredUsername,
	String email,
	String picture
)
{
}
