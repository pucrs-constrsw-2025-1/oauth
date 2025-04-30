package com.constrsw.oauth.service.providers;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakRolesResourceProvider {
    @Value("${keycloak.realm}")
    private String realm;

    @Bean
    public RolesResource rolesResource(Keycloak keycloak) {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.roles();
    }
}
