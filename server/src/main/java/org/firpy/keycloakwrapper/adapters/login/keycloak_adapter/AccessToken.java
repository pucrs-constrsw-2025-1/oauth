package org.firpy.keycloakwrapper.adapters.login.keycloak_adapter;

public record AccessToken(String tokenType, String accessToken, int expiresIn, String refreshToken, int refreshExpiresIn)
{
}
