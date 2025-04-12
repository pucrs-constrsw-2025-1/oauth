package org.firpy.keycloakwrapper.services;

import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
public class AuthorizationService
{
	public AuthorizationService(KeycloakAuthClient client)
	{
		this.keycloakAuthClient = client;
	}

	public boolean isAuthorized(String authorization, String resource, String scope)
	{
		try
		{
			MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			form.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
			form.add("audience", clientId);
			form.add("permission", "%s#%s".formatted(resource, scope));

			Map<String, Object> response = keycloakAuthClient.requestAuthorization(authorization, form);

			return response.containsKey("access_token");

		}
		catch (feign.FeignException.Forbidden exception)
		{
			return false;
		}
		catch (Exception exception)
		{
			throw new RuntimeException("Authorization check failed", exception);
		}
	}

	private final KeycloakAuthClient keycloakAuthClient;

	@Value("${keycloak.client-id}")
	private String clientId;
}

