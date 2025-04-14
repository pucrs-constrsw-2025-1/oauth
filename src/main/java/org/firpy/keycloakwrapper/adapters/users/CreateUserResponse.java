package org.firpy.keycloakwrapper.adapters.users;

public record CreateUserResponse (
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        String id
){}
