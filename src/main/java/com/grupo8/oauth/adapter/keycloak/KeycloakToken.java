package com.grupo8.oauth.adapter.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakToken(
        String access_token,
        Long expires_in,
        Long refresh_expires_in,
        String refresh_token,
        String token_type,
        @JsonProperty("not-before-policy") Long not_before_policy,
        String session_state,
        String scope) {
}
