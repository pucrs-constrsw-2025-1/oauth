package org.firpy.keycloakwrapper.setup;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.firpy.keycloakwrapper.adapters.clients.ClientRepresentation;
import org.firpy.keycloakwrapper.adapters.login.AccessToken;
import org.firpy.keycloakwrapper.adapters.login.keycloak.CreateClientRequest;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.adapters.users.CredentialRequest;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Configuration
@Slf4j
@EnableScheduling
public class ClientConfig {

    @Scheduled(initialDelay = 20000)
    public String getClientSecret() {

        if (clientSecret != null){
            return clientSecret;
        }
        log.info("Client secret not found.");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("client_id", "admin-cli");
        params.add("username", adminUsername);
        params.add("password", adminPassword);
        params.add("grant_type", "password");

        log.info("Logging in as admin.");
        AccessToken token = keycloakAuthClient.getAccessTokenWithPassword(params);
        String accessToken = "Bearer " + token.accessToken();

        log.info("Creating client with client id {}.", clientId);
        keycloakAdminClient.createClient(accessToken, new CreateClientRequest(clientId));


        ClientRepresentation clientRepresentation = keycloakAdminClient.getClients(accessToken).stream()
                .filter(client -> client.clientId().equalsIgnoreCase(clientId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client not found"));

        log.info("Creating secret for client with clientId {}.", clientId);
        CredentialRequest credentials = keycloakAdminClient.createClientSecret(accessToken, clientRepresentation.id());

        log.info("Created client secret for client id {}.", clientId);
        clientSecret = credentials.value();
        return clientSecret;
    }

    public ClientConfig(KeycloakAuthClient keycloakAuthClient, KeycloakAdminClient keycloakClient) {
        this.keycloakAuthClient = keycloakAuthClient;
        this.keycloakAdminClient = keycloakClient;
    }

    @Getter
    @Value("${keycloak.admin-username}")
    private String adminUsername;
    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Getter
    @Value("${keycloak.client-id}")
    private String clientId;
    private String clientSecret;

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;
}
