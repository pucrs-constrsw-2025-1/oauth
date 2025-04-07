package org.firpy.keycloakwrapper.setup;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakRealmAdminClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
@EnableScheduling
public class ClientConfig
{
    public ClientConfig(KeycloakRealmAdminClient keycloakRealmAdminClient, ResourceLoader resourceLoader)
    {
	    this.keycloakRealmAdminClient = keycloakRealmAdminClient;
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

        try (Keycloak keycloak = KeycloakBuilder.builder()
                       .username(adminUsername)
                       .password(adminPassword)
                       .serverUrl(keycloakUrl)
                       .realm("master")
                       .clientId(adminClientId)
                       .scope(OAuth2Constants.SCOPE_OPENID)
                       .grantType(OAuth2Constants.PASSWORD)
                       .resteasyClient(
                               new ResteasyClientBuilderImpl()
                                       .connectionPoolSize(10)
                                       .build())
                       .build())
        {
            log.info("Logged in as admin.");

            try
            {
                keycloak.realm(realmName).clients().findAll();
            }
            catch (NotFoundException notFoundException)
            {
                log.info("Realm {} doesn't exist, creating it.", realmName);
                Resource realmExport = resourceLoader.getResource("classpath:realm-export.json");
                String accessTokenString = keycloak.tokenManager().getAccessTokenString();
                keycloakRealmAdminClient.createRealm("Bearer %s".formatted(accessTokenString), realmExport.getContentAsString(StandardCharsets.UTF_8));
            }

            UserRepresentation adminUser = createAdminUser();

            RealmResource realmResource = keycloak.realm(realmName);

	        List<UserRepresentation> foundAdminUser = realmResource.users().searchByUsername(adminUser.getUsername(), true);

            if (foundAdminUser.isEmpty())
            {
                try (Response response = realmResource.users().create(adminUser))
                {
                    if (response.getStatusInfo().toEnum() != Response.Status.CREATED)
                    {
                        throw new RuntimeException("Failed to create admin user.");
                    }

                    String realmManagementClientId = "realm-management";
                    String realmManagementClientUUID = realmResource.clients().findByClientId(realmManagementClientId).getFirst().getId();
                    ClientResource realmManagementClient = realmResource.clients().get(realmManagementClientUUID);

                    RoleRepresentation realmAdminRole = realmManagementClient
                                                                .roles()
                                                                .get("realm-admin")
                                                                .toRepresentation();

                    String userUUID = realmResource.users().searchByUsername(adminUsername, true).getFirst().getId();
                    UserResource userResource = realmResource.users().get(userUUID);

                    userResource.roles()
                                .clientLevel(realmManagementClientUUID)
                                .add(Collections.singletonList(realmAdminRole));

                }
            }

            String clientUUID = realmResource.clients().findByClientId(clientId).getFirst().getId();
            ClientResource clientResource = realmResource.clients().get(clientUUID);

            clientSecret = clientResource.generateNewSecret().getSecretData();
            log.info("Created client secret for client id {}.", clientId);

            return clientSecret;
        }
    }

    public String getClientUUID(Keycloak keycloakClient)
    {
	    if (clientUUID == null)
	    {
		    clientUUID = keycloakClient.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
	    }

        return clientUUID;
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

    private String clientUUID = null;

    @Getter
    @Value("${keycloak.realm}")
    private String realmName;

    @Getter
    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    private String clientSecret;

    @Value("${keycloak.url}")
    private String keycloakUrl;

    private final KeycloakRealmAdminClient keycloakRealmAdminClient;
    private final ResourceLoader resourceLoader;
}
