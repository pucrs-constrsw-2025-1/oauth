package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.application.DTOs.GroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class GetUserGroups {

    private final KeycloakAdapter keycloakAdapter;

    public Collection<GroupDTO> run(String token, UUID userId) {
        return keycloakAdapter.getUserGroups(token, userId);
    }
}
