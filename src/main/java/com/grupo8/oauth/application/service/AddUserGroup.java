package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AddUserGroup {

    private final KeycloakAdapter keycloakAdapter;

    public void run(String token, UUID userId, String group) {
        keycloakAdapter.addUserGroup(token, userId, group);
    }
}
