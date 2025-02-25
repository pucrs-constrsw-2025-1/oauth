package org.firpy.keycloakwrapper.login;

public record LoginRequest(String clientId, String username, String password, String grantType)
{
}
