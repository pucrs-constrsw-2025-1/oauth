package org.firpy.keycloakwrapper.adapters.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenRequest(@JsonProperty(required = true) String refreshToken)
{
}
