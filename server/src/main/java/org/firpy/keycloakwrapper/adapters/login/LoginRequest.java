package org.firpy.keycloakwrapper.adapters.login;

public record LoginRequest(String clientId, String username, String password, String grantType)
{
}
