package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.adapter.keycloak.KeycloakMapper;
import com.grupo8.oauth.application.DTOs.JwtTokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RefreshToken {

    private final KeycloakAdapter keycloakAdapter;
    private final KeycloakMapper keycloakMapper;

    public JwtTokenDTO run(String refreshToken) {
        return keycloakMapper.toTokenDTO(keycloakAdapter.refreshToken(refreshToken));
    }
}
