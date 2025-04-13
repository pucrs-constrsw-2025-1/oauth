package org.firpy.keycloakwrapper.adapters.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(@JsonProperty(required = true) String username, @JsonProperty(required = true) String password)
{
}
