package com.grupo8.oauth.adapter.keycloak;

public record KeycloackCredential(
                String type,
                String value,
                Boolean temporary) {
}
