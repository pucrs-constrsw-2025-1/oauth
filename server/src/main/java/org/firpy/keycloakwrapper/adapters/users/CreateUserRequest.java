package org.firpy.keycloakwrapper.adapters.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CreateUserRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified,
        boolean enabled,
        List<Credential> credentials
) {
    public record Credential(
            String value,
            boolean temporary,
            String type,
            Integer hashIterations
    ) {}

    public CreateUserRequest(String username, String firstName, String lastName, String email, String password) {
        this(username, firstName, lastName, email, true, true, List.of(new Credential(password, false, "password", 10)) );
    }
}
