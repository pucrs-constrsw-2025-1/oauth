package org.firpy.oauth.adapters.roles;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.firpy.oauth.adapters.login.keycloak.admin.CreateRoleRequest;
import org.firpy.oauth.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("roles")
public class RolesController
{
	public RolesController(KeycloakAdminClient keycloakAdminClient)
	{
		this.keycloakAdminClient = keycloakAdminClient;
	}

	@GetMapping
	@ApiResponses
	(
		value =
		{
			@ApiResponse
			(
				responseCode = "200",
				description = "Roles retrieved",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleRepresentation.class)))
			),
			@ApiResponse
			(
				responseCode = "401",
				description = "Invalid access token",
				content = @Content(schema = @Schema(implementation = String.class, allowableValues = {"Access token is missing", "Invalid access token"}))
			),
			@ApiResponse
			(
				responseCode = "403",
				description = "Access token lacks required admin scopes",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Access token lacks required admin scopes"))
			),
			@ApiResponse
			(
				responseCode = "500",
				description = "An unexpected error occurred",
				content = @Content(schema = @Schema(implementation = String.class, format = "An unexpected error occurred: {error message}"))
			)
		}
	)
	public ResponseEntity<?> getRoles(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
	{
		if (accessToken == null || accessToken.trim().isEmpty())
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token is missing");
		}
		try (Keycloak keycloak = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			String clientUUID = keycloak.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
			RoleRepresentation[] roles = keycloak.realm(realmName).clients().get(clientUUID).roles().list().toArray(RoleRepresentation[]::new);
			return ResponseEntity.ok(roles);
		}
		catch (NotAuthorizedException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
		catch (ForbiddenException e)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access token lacks required admin scopes");
		}
		catch (Exception e)
		{
			return ResponseEntity.internalServerError().body("An unexpected error occurred: %s".formatted(e.getMessage()));
		}
	}

	@GetMapping("/{role-name}")
	@ApiResponses
	(
		value =
		{
			@ApiResponse
			(
				responseCode = "200",
				description = "Role retrieved",
				content = @Content(schema = @Schema(implementation = RoleRepresentation.class))
			),
			@ApiResponse
			(
				responseCode = "401",
				description = "Invalid access token",
				content = @Content(schema = @Schema(implementation = String.class, allowableValues = {"Access token is missing", "Invalid access token"}))
			),
			@ApiResponse
			(
				responseCode = "403",
				description = "Access token lacks required admin scopes",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Access token lacks required admin scopes"))
			),
			@ApiResponse
			(
				responseCode = "404",
				description = "Role not found",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Role not found"))
			),
			@ApiResponse
			(
				responseCode = "500",
				description = "An unexpected error occurred",
				content = @Content(schema = @Schema(implementation = String.class, format = "An unexpected error occurred: {error message}"))
			)
		}
	)
	public ResponseEntity<?> getRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		if (roleName == null || roleName.trim().isEmpty())
		{
			return ResponseEntity.badRequest().body("Role name is required");
		}
		if (accessToken == null || accessToken.trim().isEmpty())
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token is missing");
		}

		try (Keycloak keycloak = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			String clientUUID = keycloak.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
			RoleRepresentation role = keycloak.realm(realmName).clients().get(clientUUID).roles().get(roleName).toRepresentation();

			return ResponseEntity.ok(role);
		}
		catch (NotAuthorizedException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
		catch (ForbiddenException e)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access token lacks required admin scopes");
		}
		catch (NotFoundException e)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
		}
	}

	@PostMapping
	@ApiResponses
	(
		value =
		{
			@ApiResponse
			(
				responseCode = "201",
				description = "Role created",
				content = @Content(schema = @Schema(implementation = RoleRepresentation.class))
			),
			@ApiResponse
			(
				responseCode = "400",
				description = "Invalid request or email"
			),
			@ApiResponse
			(
				responseCode = "401",
				description = "Invalid access token",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Invalid access token"))
			),
			@ApiResponse
			(
				responseCode = "403",
				description = "Access token lacks required admin scopes",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Access token lacks required admin scopes"))
			),
			@ApiResponse
			(
				responseCode = "409",
				description = "Role already exists"
			),
			@ApiResponse
			(
				responseCode = "500",
				description = "An unexpected error occurred",
				content = @Content(schema = @Schema(implementation = String.class, format = "An unexpected error occurred: {error message}"))
			)
		}
	)
	public ResponseEntity<?> createRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody CreateRoleRequest role)
	{
		if (role == null || role.name() == null || role.name().trim().isEmpty())
		{
			return ResponseEntity.badRequest().body("Role name is required");
		}
		if (accessToken == null || accessToken.trim().isEmpty())
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token is missing");
		}
		try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			String clientUUID = keycloakClient.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
			RolesResource roles = keycloakClient.realm(realmName).clients().get(clientUUID).roles();
			roles.create(role.toRoleRepresentation());

			return ResponseEntity.ok(roles.get(role.name()).toRepresentation());
		}
		catch (NotAuthorizedException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
		catch (ForbiddenException e)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access token lacks required admin scopes");
		}
	}

	@DeleteMapping("/{role-name}")
	@ApiResponses
	(
		value =
		{
			@ApiResponse
			(
				responseCode = "204",
				description = "Role deleted"
			),
			@ApiResponse
			(
				responseCode = "401",
				description = "Invalid access token",
				content = @Content(schema = @Schema(implementation = String.class, allowableValues = {"Access token is missing", "Invalid access token"}))
			),
			@ApiResponse
			(
				responseCode = "403",
				description = "Access token lacks required admin scopes",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Access token lacks required admin scopes"))
			),
			@ApiResponse
			(
				responseCode = "404",
				description = "Role not found",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Role not found"))
			),
			@ApiResponse
			(
				responseCode = "500",
				description = "An unexpected error occurred",
				content = @Content(schema = @Schema(implementation = String.class, format = "An unexpected error occurred: {error message}"))
			)
		}
	)
	public ResponseEntity<?> deleteRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName)
	{
		if (roleName == null || roleName.trim().isEmpty())
		{
			return ResponseEntity.badRequest().body("Role name is required");
		}
		if (accessToken == null || accessToken.trim().isEmpty())
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token is missing");
		}
		try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			String clientUUID = keycloakClient.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
			RolesResource roles = keycloakClient.realm(realmName).clients().get(clientUUID).roles();
			roles.deleteRole(roleName);

			return ResponseEntity.noContent().build();
		}
		catch (NotAuthorizedException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
		catch (ForbiddenException e)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access token lacks required admin scopes");
		}
		catch (NotFoundException e)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
		}
		catch (Exception e)
		{
			return ResponseEntity.internalServerError().body("An unexpected error occurred: %s".formatted(e.getMessage()));
		}
	}

	@PutMapping("/{role-name}")
	@ApiResponses
	(
		value =
		{
			@ApiResponse
			(
				responseCode = "204",
				description = "Role updated"
			),
			@ApiResponse
			(
				responseCode = "400",
				description = "Invalid request or email"
			),
			@ApiResponse
			(
				responseCode = "401",
				description = "Invalid access token",
				content = @Content(schema = @Schema(implementation = String.class, allowableValues = {"Access token is missing", "Invalid access token"}))
			),
			@ApiResponse
			(
				responseCode = "403",
				description = "Access token lacks required admin scopes",
				content = @Content(schema = @Schema(implementation = String.class, defaultValue = "Access token lacks required admin scopes"))
			),
			@ApiResponse
			(
				responseCode = "404",
				description = "Role not found"
			),
			@ApiResponse
			(
				responseCode = "500",
				description = "An unexpected error occurred",
				content = @Content(schema = @Schema(implementation = String.class, format = "An unexpected error occurred: {error message}"))
			)
		}
	)
	public ResponseEntity<?> updateRole(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("role-name") String roleName, @RequestBody CreateRoleRequest role)
	{
		if (accessToken == null || accessToken.trim().isEmpty())
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token is missing");
		}
		if (roleName == null || roleName.trim().isEmpty())
		{
			return ResponseEntity.badRequest().body("Role name is required");
		}

		try (Keycloak keycloak = keycloakAdminClient.fromAdminAccessToken(accessToken))
		{
			String clientUUID = keycloak.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
			RolesResource roles = keycloak.realm(realmName).clients().get(clientUUID).roles();
			roles.get(roleName).update(role.toRoleRepresentation());

			return ResponseEntity.noContent().build();
		}
		catch (NotAuthorizedException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
		catch (ForbiddenException e)
		{
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access token lacks required admin scopes");
		}
		catch (NotFoundException e)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
		}
		catch (Exception e)
		{
			return ResponseEntity.internalServerError().body("An unexpected error occurred: %s".formatted(e.getMessage()));
		}
	}

	private final KeycloakAdminClient keycloakAdminClient;

	@Value("${keycloak.realm}")
	private String realmName;

	@Value("${keycloak.client-id}")
	private String clientId;
}
