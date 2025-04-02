package org.firpy.keycloakwrapper.adapters.roles;

import io.swagger.v3.oas.annotations.media.Schema;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("roles")
public class RolesController
{
	public RolesController(KeycloakAdminClient keycloakAdminClient)
	{
		this.keycloakAdminClient = keycloakAdminClient;
	}

	@GetMapping
	public RoleRepresentation[] getRoles(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
	{
		return keycloakAdminClient.getClientRoles(accessToken, clientId);
	}

	@GetMapping("/{role-name}")
	public RoleRepresentation getRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		return keycloakAdminClient.getClientRole(accessToken, roleName, clientId);
	}

	@PostMapping
	public ResponseEntity<Void> createRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody RoleRepresentation role)
	{
		keycloakAdminClient.createClientRole(accessToken, clientId, role);

		return ResponseEntity.created(URI.create("/roles/%s".formatted(role.name())))
							 .build();
	}

	@DeleteMapping("/{role-name}")
	public ResponseEntity<Void> deleteRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		keycloakAdminClient.deleteClientRole(accessToken, roleName, clientId);

		return ResponseEntity.noContent()
						     .build();
	}

	@PutMapping("/{role-name}")
	public void updateRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName, @RequestBody RoleRepresentation role)
	{
		keycloakAdminClient.updateClientRole(accessToken, roleName, clientId, role);
	}

	private final KeycloakAdminClient keycloakAdminClient;

	@Value("${keycloak.client-id}")
	private String clientId;
}
