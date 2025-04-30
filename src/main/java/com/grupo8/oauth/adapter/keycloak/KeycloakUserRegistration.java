package com.grupo8.oauth.adapter.keycloak;

import java.util.Collection;

public record KeycloakUserRegistration(
                String email,
                String username,
                String firstName,
                String lastName,
                Boolean enabled,
                Collection<KeycloackCredential> credentials) {
}
