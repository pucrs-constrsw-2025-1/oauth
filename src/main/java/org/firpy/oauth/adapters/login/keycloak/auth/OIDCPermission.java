package org.firpy.oauth.adapters.login.keycloak.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OIDCPermission
(
	@JsonProperty("resource_id") String resourceId,
	@JsonProperty("resource_name") String resourceName
)
{
}
