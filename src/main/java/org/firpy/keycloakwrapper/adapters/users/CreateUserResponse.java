package org.firpy.keycloakwrapper.adapters.users;

public record CreateUserResponse (
        String email,
        String firstName,
        String lastName,
        String password,
        String id
){}
