package org.firpy.keycloakwrapper.adapters.login;

public record LoginResponse(String tokenType, String accessToken, int expiresIn, String refreshToken, int refreshExpiresIn)
{
}
