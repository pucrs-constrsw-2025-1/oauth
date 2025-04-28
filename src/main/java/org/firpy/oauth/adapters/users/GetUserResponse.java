package org.firpy.oauth.adapters.users;

public record GetUserResponse(
        String email,
        String firstName,
        String lastName,
        Boolean enabled,
        String id
)
{
}