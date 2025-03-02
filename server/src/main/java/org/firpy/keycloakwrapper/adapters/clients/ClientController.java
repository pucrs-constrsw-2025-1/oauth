package org.firpy.keycloakwrapper.adapters.clients;

import org.firpy.keycloakwrapper.adapters.login.keycloak.CreateClientRequest;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createClient(@RequestHeader("Authorization") String accessToken)
    {
		CreateClientRequest createClientRequest = new CreateClientRequest(clientId);
	    keycloakClient.createClient(accessToken, createClientRequest);

		return ResponseEntity.ok(HttpStatus.CREATED);
    }


    private final KeycloakAdminClient keycloakClient;

	@Value("${keycloak.client-id}")
	private String clientId;
}
