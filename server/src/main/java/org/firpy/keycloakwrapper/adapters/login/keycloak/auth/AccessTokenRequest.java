package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenRequest(
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("grant_type") String grantType
) {
}