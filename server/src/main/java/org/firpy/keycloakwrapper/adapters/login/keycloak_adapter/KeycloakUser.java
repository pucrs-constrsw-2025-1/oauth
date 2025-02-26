package org.firpy.keycloakwrapper.adapters.login.keycloak_adapter;

public record KeycloakUser(String sub, boolean emailVerified, String name, String preferredUsername, String givenName, String familyName, String email)
{
}
