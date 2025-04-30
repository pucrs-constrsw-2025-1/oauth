package com.grupo8.oauth.application.DTOs;

public record JwtTokenDTO(
        String access_token,
        Long expires_in,
        Long refresh_expires_in,
        String refresh_token,
        String token_type,
        Long not_before_policy,
        String session_state,
        String scope) {
}
