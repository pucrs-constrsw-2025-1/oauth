package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

public record AccessTokenRequest(String clientId, String clientSecret, String username, String password, String grantType)
{
}
