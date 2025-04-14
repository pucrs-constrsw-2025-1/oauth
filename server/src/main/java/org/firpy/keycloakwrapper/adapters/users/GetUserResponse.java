package org.firpy.keycloakwrapper.adapters.users;

public record GetUserResponse(
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean enabled,
        String id
){}