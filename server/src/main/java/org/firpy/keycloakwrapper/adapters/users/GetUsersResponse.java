package org.firpy.keycloakwrapper.adapters.users;

public record GetUsersResponse (
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean enabled,
        String id
){}