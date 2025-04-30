package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.application.DTOs.GroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class GetGroups {

    private final KeycloakAdapter keycloakAdapter;

    public Collection<GroupDTO> run(String token) {
        return keycloakAdapter.getGroups(token);
    }
}
