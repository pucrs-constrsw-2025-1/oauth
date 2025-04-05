package org.firpy.keycloakwrapper.setup;

import feign.FeignException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.firpy.keycloakwrapper.adapters.login.AccessToken;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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
import java.util.Collections;
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

    @Scheduled(initialDelay = 40000)
    public String getClientSecret() throws IOException
    {
        if (clientSecret != null)
        {
            return clientSecret;
        }

        log.info("Client secret not found.");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("client_id", adminClientId);
        params.add("username", adminUsername);
        params.add("password", adminPassword);
        params.add("grant_type", "password");

        log.info("Logging in as admin.");
        AccessToken token = keycloakAuthClient.getAccessTokenWithPassword(params, "master");
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

        log.info("Client secret not found.");

        Keycloak keycloakClient = KeycloakBuilder.builder()
                                          .serverUrl(keycloakUrl)
                                          .realm("master")
                                          .username("admin")
                                          .password("admin")
                                          .clientId("admin-cli")
                                          .resteasyClient(
                                                  new ResteasyClientBuilderImpl()
                                                          .connectionPoolSize(10)
                                                          .build()
                                          ).build();

        UserRepresentation user = createAdminUser();

        RealmResource realmResource = keycloakClient.realm(realmName);
	    realmResource.users().create(user);

        String realmManagementClientId = "realm-management";
        String realmManagementClientUUID = realmResource.clients().findByClientId(realmManagementClientId).getFirst().getId();
        ClientResource realmManagementClient = realmResource.clients().get(realmManagementClientUUID);

        RoleRepresentation realmAdminRole = realmManagementClient
                                                    .roles()
                                                    .get("realm-admin")
                                                    .toRepresentation();

	    String userUUID = realmResource.users().searchByUsername("admin", true).getFirst().getId();
        UserResource userResource = realmResource.users().get(userUUID);

        userResource.roles()
                    .clientLevel(realmManagementClientUUID)
                    .add(Collections.singletonList(realmAdminRole));

        String clientUUID = realmResource.clients().findByClientId(clientId).getFirst().getId();
        ClientResource clientResource = realmResource.clients().get(clientUUID);

        clientSecret = clientResource.generateNewSecret().getSecretData();
        log.info("Created client secret for client id {}.", clientId);

        return clientSecret;
    }

    private static UserRepresentation createAdminUser()
    {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("admin");
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setEmail("s.firpo@edu.pucrs.br");
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("admin");
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        return user;
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

    private String clientSecret;

    @Value("${keycloak.url}")
    private String keycloakUrl;

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final ResourceLoader resourceLoader;
}
