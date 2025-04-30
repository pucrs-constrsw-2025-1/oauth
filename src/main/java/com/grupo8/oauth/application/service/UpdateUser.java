package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.adapter.keycloak.KeycloakMapper;
import com.grupo8.oauth.application.DTOs.UserDTO;
import com.grupo8.oauth.application.DTOs.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateUser {

    private final KeycloakAdapter keycloakAdapter;
    private final KeycloakMapper keycloakMapper;

    public UserDTO run(String token, UUID id, UserRequestDTO user) {
        return keycloakMapper
                .toUserDTO(keycloakAdapter.updateUser(token, id, keycloakMapper.toKeycloakUserRegistration(user)));
    }
}
