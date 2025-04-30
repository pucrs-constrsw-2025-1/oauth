package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.application.DTOs.PermissionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class ValidateToken {

    private final KeycloakAdapter keycloakAdapter;

    public void run(String accessToken, String resource, String method) {
        Collection<PermissionDTO> permissions = keycloakAdapter.getUserPermissions(accessToken);
        var foundPermission = permissions.stream()
                .filter(permission -> permission.getName().equals(resource))
                .findFirst();
        if (method.equals(HttpMethod.GET.name()) || foundPermission.isPresent()) {
            return;
        }
        throw new WebClientResponseException(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null,
                null, null);
    }

}