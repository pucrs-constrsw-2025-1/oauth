package org.firpy.keycloakwrapper.adapters.clients;

import org.firpy.keycloakwrapper.adapters.login.keycloak.CreateClientRequest;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController()
@RequestMapping("clients")
public class ClientController
{
	public ClientController(KeycloakAdminClient keycloakClient)
	{
		this.keycloakClient = keycloakClient;
	}

	/**
     * Consumir a rota POST {{base-keycloak-url}}/admin/realms/{{realm}}/clients da REST API
     * do Keycloak para criar um Client público.
     * @param accessToken
     */
    @PostMapping()
    public ResponseEntity<Void> createClient(@RequestHeader("Authorization") String accessToken)
    {
		CreateClientRequest createClientRequest = new CreateClientRequest(clientId);
	    keycloakClient.createClient(accessToken, createClientRequest);

		return ResponseEntity.created(URI.create("/admin/realms/${keycloak.realm}/clients/%s".formatted(clientId))).build();
    }


    private final KeycloakAdminClient keycloakClient;

	@Value("${keycloak.client-id}")
	private String clientId;
}
