package org.firpy.keycloakwrapper.adapters.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(@JsonProperty(required = true) String email, @JsonProperty(required = true) String password)
{
}
