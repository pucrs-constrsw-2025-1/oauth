package org.firpy.keycloakwrapper.adapters.login.keycloak;

public record CreateClientRequest(
        String id,
        String name,
        String protocol,
        Boolean publicClient,

        Boolean directAccessGrantsEnabled,
        Boolean authorizationServicesEnabled,
        Boolean serviceAccountsEnabled
) {
    public CreateClientRequest(String name) {
        this(name, name,"openid-connect", false, true, true, true);
    }
}
