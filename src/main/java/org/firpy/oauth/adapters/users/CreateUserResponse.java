package org.firpy.oauth.adapters.users;

public record CreateUserResponse (
        String email,
        String firstName,
        String lastName,
        String password,
        String id
){}
