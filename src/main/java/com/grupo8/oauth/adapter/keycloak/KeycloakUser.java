package com.grupo8.oauth.adapter.keycloak;

import java.util.UUID;

public record KeycloakUser(UUID id, String email, String firstName, String lastName, Boolean enabled) {

}
