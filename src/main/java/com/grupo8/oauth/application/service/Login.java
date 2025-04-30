package com.grupo8.oauth.application.service;

import org.springframework.stereotype.Component;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.adapter.keycloak.KeycloakMapper;
import com.grupo8.oauth.application.DTOs.JwtTokenDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Login {

    private final KeycloakAdapter keycloakAdapter;

    private final KeycloakMapper keycloakMapper;

    public JwtTokenDTO run(String user, String password) {
        return keycloakMapper.toTokenDTO(keycloakAdapter.authenticateUser(user, password));
    }

}