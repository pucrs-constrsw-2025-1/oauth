package com.grupo8.oauth.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.adapter.keycloak.KeycloakMapper;
import com.grupo8.oauth.adapter.keycloak.KeycloakUser;
import com.grupo8.oauth.application.DTOs.UserDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class GetUser {

    private final KeycloakAdapter keycloakAdapter;

    private final KeycloakMapper keycloakMapper;

    public UserDTO run(String token, UUID id) {
        KeycloakUser user = keycloakAdapter.getUserById(token, id);
        return keycloakMapper.toUserDTO(user);
    }

}
