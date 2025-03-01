package org.firpy.keycloakwrapper.adapters.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CreateUserRequest(
        @JsonProperty("username") String username,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("emailVerified") boolean emailVerified,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("credentials") List<Credential> credentials
) {
    public record Credential(
            @JsonProperty("value") String value,
            @JsonProperty("temporary") boolean temporary
    ) {}

    public CreateUserRequest(String username, String firstName, String lastName, String email, String password) {
        this(username, firstName, lastName, email, true, true, List.of(new Credential(password, false)) );
    }
}
