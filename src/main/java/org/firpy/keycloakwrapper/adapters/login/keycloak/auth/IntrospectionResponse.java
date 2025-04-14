package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public record IntrospectionResponse
(
	@JsonProperty(required = false) OIDCPermission[] permissions,
	@JsonProperty(required = false) int exp,
	@JsonProperty(required = false)  int nbf,
	@JsonProperty(required = false) int iat,
	@JsonProperty(required = false) String aud,
	@JsonProperty(required = true) boolean active
)
{
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof IntrospectionResponse(OIDCPermission[] permissions1, int exp1, int nbf1, int iat1, String aud1, boolean active1
		)))
		{
			return false;
		}
		return exp == exp1
		    && nbf == nbf1
		    && iat == iat1
		    && active == active1
		    && Objects.equals(aud, aud1)
		    && Objects.deepEquals(permissions, permissions1);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(Arrays.hashCode(permissions), exp, nbf, iat, aud, active);
	}

	@Override
	public String toString()
	{
		return "IntrospectionResponse{permissions=%s, exp=%d, nbf=%d, iat=%d, aud='%s', active=%s}"
			.formatted(Arrays.toString(permissions), exp, nbf, iat, aud, active);
	}
}
