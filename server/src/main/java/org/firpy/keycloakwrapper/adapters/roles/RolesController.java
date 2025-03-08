package org.firpy.keycloakwrapper.adapters.roles;

import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.RoleRepresentation;
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
	public RoleRepresentation[] getRoles(@RequestHeader(value = "Authorization", required = false) String accessToken)
	{
		return keycloakAdminClient.getRealmRoles(accessToken);
	}

	@GetMapping("/{role-name}")
	public RoleRepresentation getRole(@RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		return keycloakAdminClient.getRealmRole(accessToken, roleName);
	}

	@PostMapping
	public ResponseEntity<Void> createRole(@RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody RoleRepresentation role)
	{
		keycloakAdminClient.createRealmRole(accessToken, role);
		return ResponseEntity.created(URI.create("/roles/%s".formatted(role.name())))
							 .build();
	}

	@DeleteMapping("/{role-name}")
	public ResponseEntity<Void> deleteRole(@RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		keycloakAdminClient.deleteRealmRole(accessToken, roleName);
		return ResponseEntity.noContent()
						     .build();
	}

	@PutMapping("/{role-name}")
	public void updateRole(@RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName, @RequestBody RoleRepresentation role)
	{
		keycloakAdminClient.updateRealmRole(accessToken, roleName, role);
	}

	private final KeycloakAdminClient keycloakAdminClient;
}
