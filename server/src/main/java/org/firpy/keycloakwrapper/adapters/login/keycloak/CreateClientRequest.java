package org.firpy.keycloakwrapper.adapters.login.keycloak;

public record CreateClientRequest(
        String id,
        String name,
        Boolean directAccessGrantsEnabled,
        Boolean authorizationServicesEnabled
) {
    public CreateClientRequest(String name) {
        this(name, name, true, true);
    }
}
