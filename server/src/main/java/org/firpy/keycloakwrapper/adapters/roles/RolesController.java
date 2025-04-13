package org.firpy.keycloakwrapper.adapters.roles;

import io.swagger.v3.oas.annotations.media.Schema;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.CreateRoleRequest;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("roles")
public class RolesController
{
	public RolesController(KeycloakAdminClient keycloakAdminClient, ClientConfig clientConfig)
	{
		this.keycloakAdminClient = keycloakAdminClient;
		this.clientConfig = clientConfig;
	}

	@GetMapping
	public ResponseEntity<?> getRoles(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
	{
		try (Keycloak keycloak = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			RoleRepresentation[] roles = keycloak.realm(realmName).clients().get(clientConfig.getClientUUID(keycloak)).roles().list().toArray(RoleRepresentation[]::new);
			return ResponseEntity.ok(roles);
		}
	}

	@GetMapping("/{role-name}")
	public ResponseEntity<?> getRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		try (Keycloak keycloak = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			RoleRepresentation role = keycloak.realm(realmName).clients().get(clientConfig.getClientUUID(keycloak)).roles().get(roleName).toRepresentation();
			return ResponseEntity.ok(role);
		}
	}

	@PostMapping
	public ResponseEntity<?> createRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody CreateRoleRequest role)
	{
		try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			RolesResource roles = keycloakClient.realm(realmName).clients().get(clientConfig.getClientUUID(keycloakClient)).roles();
			roles.create(role.toRoleRepresentation());
			return ResponseEntity.ok(roles.get(role.name()).toRepresentation());
		}
	}

	@DeleteMapping("/{role-name}")
	public ResponseEntity<?> deleteRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			RolesResource roles = keycloakClient.realm(realmName).clients().get(clientConfig.getClientUUID(keycloakClient)).roles();
			roles.deleteRole(roleName);
			return ResponseEntity.noContent().build();
		}
	}

	@PutMapping("/{role-name}")
	public ResponseEntity<?> updateRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName, @RequestBody CreateRoleRequest role)
	{
		try (Keycloak keycloak = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			RolesResource roles = keycloak.realm(realmName).clients().get(clientConfig.getClientUUID(keycloak)).roles();
			roles.get(roleName).update(role.toRoleRepresentation());
			return ResponseEntity.noContent().build();
		}
	}

	private final KeycloakAdminClient keycloakAdminClient;

	private final ClientConfig clientConfig;

	@Value("${keycloak.realm}")
	private String realmName;
}
