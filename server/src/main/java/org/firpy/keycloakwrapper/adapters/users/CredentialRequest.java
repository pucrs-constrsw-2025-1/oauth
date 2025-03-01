package org.firpy.keycloakwrapper.adapters.users;

public record CredentialRequest(
        String value,
        boolean temporary,
        String type,
        Integer hashIterations
) {
    public CredentialRequest(String value) {
        this(value, false, "password", 10);
    }
}
