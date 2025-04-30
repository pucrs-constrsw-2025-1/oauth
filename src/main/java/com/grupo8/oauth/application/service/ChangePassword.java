package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloackCredential;
import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ChangePassword {

    private final KeycloakAdapter keycloakAdapter;

    public void run(String token, UUID userId, String password) {
        keycloakAdapter.changePassword(token, userId, new KeycloackCredential("password", password, false));
    }

}
