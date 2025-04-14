package org.firpy.keycloakwrapper.adapters.login.keycloak.admin;

import org.firpy.keycloakwrapper.seeds.RealmSeed;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KeycloakAdminClient
{
	public KeycloakAdminClient(RealmSeed realmSeed)
	{
		this.realmSeed = realmSeed;
	}

	public Keycloak fromAdminAccessToken(String accessToken)
	{
		return KeycloakBuilder.builder()
						      .authorization(accessToken)
							  .serverUrl(keycloakUrl)
							  .realm(realmName)
							  .clientId(adminClientId)
							  .resteasyClient(
									  new ResteasyClientBuilderImpl()
										  .connectionPoolSize(10)
										  .build())
							  .build();
	}

	public Keycloak fromClientSecret() throws IOException
	{
		return KeycloakBuilder.builder()
							  .clientId(clientId)
						      .clientSecret(realmSeed.getClientSecret())
							  .serverUrl(keycloakUrl)
							  .realm(realmName)
							  .resteasyClient(
									  new ResteasyClientBuilderImpl()
										  .connectionPoolSize(10)
										  .build()).build();
	}

	private final RealmSeed realmSeed;

	@Value("${keycloak.realm}")
	private String realmName;

	@Value("${keycloak.admin-client-id}")
	private String adminClientId;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${keycloak.url}")
	private String keycloakUrl;
}
