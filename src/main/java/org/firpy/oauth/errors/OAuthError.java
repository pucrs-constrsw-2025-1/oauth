package org.firpy.oauth.errors;

public record OAuthError
        (
                String errorDescription,
                String source
        )
{
    public static OAuthError keycloakError(String errorDescription)
    {
        return new OAuthError(errorDescription, "Keycloak");
    }

    public static OAuthError internalError(String errorDescription)
    {
        return new OAuthError(errorDescription, "oauth");
    }
}
