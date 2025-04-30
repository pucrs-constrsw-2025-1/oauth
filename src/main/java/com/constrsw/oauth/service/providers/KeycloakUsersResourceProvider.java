package com.constrsw.oauth.service.providers;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakUsersResourceProvider {

    @Value("${keycloak.realm}")
    private String realm;

    @Bean
    public UsersResource usersResource(Keycloak keycloak) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            return realmResource.users();
        } catch (Exception e) {
            System.err.println("Erro ao acessar Keycloak: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }
}
