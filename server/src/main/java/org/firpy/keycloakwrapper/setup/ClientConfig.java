package org.firpy.keycloakwrapper.setup;

import feign.FeignException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.firpy.keycloakwrapper.adapters.clients.ClientRepresentation;
import org.firpy.keycloakwrapper.adapters.login.AccessToken;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.RoleRepresentation;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUser;
import org.firpy.keycloakwrapper.adapters.users.CreateKeycloakUserRequest;
import org.firpy.keycloakwrapper.adapters.users.CredentialRequest;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
@EnableScheduling
public class ClientConfig
{
    public ClientConfig(KeycloakAuthClient keycloakAuthClient, KeycloakAdminClient keycloakClient, ResourceLoader resourceLoader)
    {
        this.keycloakAuthClient = keycloakAuthClient;
        this.keycloakAdminClient = keycloakClient;
	    this.resourceLoader = resourceLoader;
    }

    @Scheduled(initialDelay = 25000)
    public String getClientSecret() throws IOException
    {
        if (clientSecret != null)
        {
            return clientSecret;
        }

        log.info("Client secret not found.");

        log.info("Logging in as admin.");
        AccessToken token = keycloakAuthClient.getAccessTokenWithPassword(getAdminLoginParameters(), "master");
        String accessToken = "Bearer %s".formatted(token.accessToken());

        try
        {
            keycloakAdminClient.getRealm(accessToken, realmName);
        }
        catch (FeignException.NotFound exception)
        {
            log.info("Realm {} doesn't exist, creating it.", realmName);
            Resource realmExport = resourceLoader.getResource("classpath:realm-export.json");
	        keycloakAdminClient.createRealm(accessToken, realmExport.getContentAsString(StandardCharsets.UTF_8));
        }

        ClientRepresentation clientRepresentation = keycloakAdminClient.getClients(accessToken, keycloakRealm).stream()
                .filter(client -> client.clientId().equalsIgnoreCase(clientId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client not found"));

        log.info("Creating secret for client with clientId {}.", clientId);
        CredentialRequest credentials = keycloakAdminClient.createClientSecret(accessToken, clientRepresentation.id(), keycloakRealm);

        log.info("Created client secret for client id {}.", clientId);
        clientSecret = credentials.value();
        return clientSecret;
    }

    //@Scheduled(initialDelay = 35000)
    //TODO make this work
    public void createAdminUser() throws IOException {
        AccessToken adminCliAccessToken = keycloakAuthClient.getAccessTokenWithPassword(getAdminLoginParameters(), "master");

        //TODO Absolutely no idea why I'm getting a 401 here
        ClientRepresentation clientRepresentation = keycloakAdminClient.getClients(adminCliAccessToken.accessToken(), "master").stream()
                .filter(client -> client.clientId().equalsIgnoreCase("security-admin-console"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client not found"));

        CredentialRequest credentials = keycloakAdminClient.createClientSecret(adminCliAccessToken.accessToken(), clientRepresentation.id(), "master");
        String securityAdminClientSecret = credentials.value();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", "security-admin-console");
        params.add("client_secret", securityAdminClientSecret);

        AccessToken securityAdminConsoleAccessToken = keycloakAuthClient.getAccessTokenWithPassword(params, "master");

        keycloakAdminClient.createUser(securityAdminConsoleAccessToken.accessToken(), new CreateKeycloakUserRequest(adminUsername, "admin", "admin", "admin@gmail.com", adminPassword));
        log.info(adminCliAccessToken.accessToken());

        KeycloakUser[] users = keycloakAdminClient.getUsers(securityAdminConsoleAccessToken.accessToken());
        KeycloakUser adminUser = Arrays.stream(users).filter(user -> user.username().equalsIgnoreCase(adminUsername)).findFirst().orElseThrow();

        keycloakAdminClient.createUserRoleMappings(securityAdminConsoleAccessToken.accessToken(), adminUser.id(), new RoleRepresentation[]{new RoleRepresentation("af93bbfd-df3f-4b38-a47e-f5c80e909019", "realm-admin", "Realm Admin", false, false, false, realmName)});
    }

    public MultiValueMap<String, ?> getAdminLoginParameters() throws IOException
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("client_id", adminClientId);
        params.add("username", adminUsername);
        params.add("password", adminPassword);
        params.add("grant_type", "password");

        return params;
    }

    @Getter
    @Value("${keycloak.admin-username}")
    private String adminUsername;
    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Getter
    @Value("${keycloak.client-id}")
    private String clientId;

    @Getter
    @Value("${keycloak.realm}")
    private String realmName;

    @Getter
    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    private String clientSecret;

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final ResourceLoader resourceLoader;
}
