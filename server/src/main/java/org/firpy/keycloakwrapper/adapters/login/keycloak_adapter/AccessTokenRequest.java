package org.firpy.keycloakwrapper.adapters.login.keycloak_adapter;

public record AccessTokenRequest(String clientId, String clientSecret, String username, String password, String grantType)
{
}
