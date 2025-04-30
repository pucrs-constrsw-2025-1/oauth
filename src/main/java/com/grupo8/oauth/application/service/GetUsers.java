package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.adapter.keycloak.KeycloakMapper;
import com.grupo8.oauth.adapter.keycloak.KeycloakUser;
import com.grupo8.oauth.application.DTOs.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class GetUsers {

    private final KeycloakAdapter keycloakAdapter;

    private final KeycloakMapper keycloakMapper;

    public Collection<UserDTO> run(String token, Boolean enabled) {
        Collection<KeycloakUser> users = keycloakAdapter.getUsers(token);
        if (Objects.nonNull(enabled)) {
            return users.stream()
                    .filter(user -> user.enabled() == enabled)
                    .map(keycloakMapper::toUserDTO)
                    .toList();
        }

        return users.stream().map(keycloakMapper::toUserDTO).toList();
    }
}
