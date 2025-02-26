package org.firpy.keycloakwrapper.adapters.login;

public record AccessToken(String tokenType, String accessToken, int expiresIn, String refreshToken, int refreshExpiresIn)
{
}
