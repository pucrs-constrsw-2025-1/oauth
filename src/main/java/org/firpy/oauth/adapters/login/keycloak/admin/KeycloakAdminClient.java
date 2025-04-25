package org.firpy.oauth.adapters.login.keycloak.admin;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAdminClient
{
	public Keycloak fromAdminAccessToken(String accessToken)
	{
		return KeycloakBuilder.builder()
						      .authorization(accessToken)
							  .serverUrl(keycloakUrl)
							  .realm(realmName)
							  .clientId(clientId)
							  .resteasyClient(
									  new ResteasyClientBuilderImpl()
										  .connectionPoolSize(10)
										  .build())
							  .build();
	}

	@Value("${keycloak.realm}")
	private String realmName;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${keycloak.url}")
	private String keycloakUrl;
}
