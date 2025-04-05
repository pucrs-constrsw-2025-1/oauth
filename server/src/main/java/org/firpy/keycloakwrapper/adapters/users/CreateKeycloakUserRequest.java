package org.firpy.keycloakwrapper.adapters.users;

import java.util.List;

public record CreateKeycloakUserRequest
(
    String username,
    String firstName,
    String lastName,
    String email,
    boolean emailVerified,
    boolean enabled,
    List<CredentialRequest> credentials
)
{
    public CreateKeycloakUserRequest(String username, String firstName, String lastName, String email, String password)
    {
        this(username, firstName, lastName, email, false, true, List.of(new CredentialRequest(password)) );
    }

    public CreateKeycloakUserRequest(String username, String firstName, String lastName, String password)
    {
        this(username, firstName, lastName, null, true, true, List.of(new CredentialRequest(password)) );
    }
}
