package org.firpy.keycloakwrapper.adapters.users;

import java.util.List;

public record UpdateKeycloakUserRequest
(
    String firstName,
    String lastName,
    String email,
    boolean emailVerified,
    boolean enabled,
    List<CredentialRequest> credentials
)
{
    public UpdateKeycloakUserRequest(String firstName, String lastName, String email, String password)
    {
        this(firstName, lastName, email, true, true, List.of(new CredentialRequest(password)) );
    }
}
