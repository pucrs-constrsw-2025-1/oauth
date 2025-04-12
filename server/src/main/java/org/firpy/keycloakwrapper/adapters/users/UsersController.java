package org.firpy.keycloakwrapper.adapters.users;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUserInfo;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.firpy.keycloakwrapper.utils.WebApplicationResponseUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("users")
public class UsersController
{
	public UsersController(ClientConfig clientConfig, KeycloakAdminClient keycloakAdminClient, KeycloakAuthClient keycloakAuthClient)
	{
		this.clientConfig = clientConfig;
		this.keycloakAdminClient = keycloakAdminClient;
		this.keycloakAuthClient = keycloakAuthClient;
	}

	/**
     * Consumir a rota do Keycloak que recupera todos os usuários
     * @param accessToken
     * @return
     */
    @GetMapping()
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetUsersResponse.class)))),
        @ApiResponse(
            responseCode = "500",
            description = "An unexpected error occurred",
            content = @Content(schema = @Schema(implementation = String.class)))
        }
    )
    public ResponseEntity<?> getUsers(
            @Schema(hidden = true)
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            UsersResource usersResource = keycloakClient.realm(realmName).users();

            List<GetUsersResponse> users = usersResource.search(
                    username,
                    firstName,
                    lastName,
                    email,
                    0, 10000,
                    enabled,
                    null
            ).stream().map( x -> new GetUsersResponse(
                    x.getUsername(),
                    x.getFirstName(),
                    x.getLastName(),
                    x.getEmail(),
                    x.isEnabled(),
                    x.getId()
                    )
            ).toList();

            return ResponseEntity.ok(users);
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
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }


    /**
     * Consumir a rota do Keycloak que recupera um usuário a partir do seu id
     * @param id
     * @param accessToken
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") String id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
	        UserRepresentation user = keycloakClient.realm(realmName).users().get(id).toRepresentation();
            return ResponseEntity.ok(user);
        }
    }

    @GetMapping("/current")
    public KeycloakUserInfo getCurrentUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
        return keycloakAuthClient.getCurrentUser(accessToken);
    }

    /**
     * Consumir a rota do Keycloak que cria um novo usuário
     * @param accessToken
     * @return
     */
    @PostMapping()
    public ResponseEntity<?> createUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody CreateUserRequest createUserRequest)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            try (Response response = keycloakClient.realm(realmName).users().create(createUserRequest.toKeycloakUserRepresentation()))
            {
                int status = response.getStatus();
                URI location = response.getLocation();

                if (status == 201 && location != null)
                {
                    String[] segments = location.getPath().split("/");
                    String userId = segments[segments.length - 1];

                    return ResponseEntity.created(location).body(createUserRequest.toResponse(userId));
                }

                return switch (status)
                {
                    case 400 -> ResponseEntity.badRequest().body("Invalid request or email");
                    case 409 -> ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
                    case 401 -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
                    case 403 -> ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access token lacks required admin scopes");
                    default -> ResponseEntity.status(status).body("Unexpected error");
                };
            }
        }
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PUT)
     * @param accessToken
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody UpdateUserRequest updateUserRequest)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            keycloakClient.realm(realmName).users().get(id).update(updateUserRequest.toKeycloakUserRepresentation());
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PATCH)
     * @param accessToken
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserPassword(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody UpdateUserPasswordRequest passwordReset)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setValue(passwordReset.newPassword());
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setTemporary(false);
            keycloakClient.realm(realmName).users().get(id).resetPassword(credentialRepresentation);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken)) {
            UserResource userResource = keycloakClient.realm(realmName).users().get(id);
            UserRepresentation user = userResource.toRepresentation();


            user.setEnabled(false);
            userResource.update(user);

            return ResponseEntity.ok("User disabled successfully");
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
            return ResponseEntity.internalServerError().body("Failed to disable user: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/role-mappings")
    public ResponseEntity<?> getUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            MappingsRepresentation allRoles = keycloakClient.realm(realmName).users().get(id).roles().getAll();
            Map<String, ClientMappingsRepresentation> clientRoleMappings = allRoles.getClientMappings();
            List<RoleRepresentation> allRolesList = (clientRoleMappings != null ? clientRoleMappings.values().stream().flatMap(clientMappings -> clientMappings.getMappings().stream()).toList() : List.of());

            return ResponseEntity.ok(allRolesList.toArray(RoleRepresentation[]::new));
        }
    }

    @PostMapping("/{id}/role-mappings")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Role mappings created")})
    public ResponseEntity<?> createUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody String[] roleNamesToAdd)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            String clientUUID = clientConfig.getClientUUID(keycloakClient);
            List<RoleRepresentation> availableRoles = keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).listAvailable();
            List<RoleRepresentation> availableRolesToAdd = availableRoles.stream().filter(role -> Arrays.stream(roleNamesToAdd).toList().contains(role.getName())).toList();
            keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).add(availableRolesToAdd);
            return ResponseEntity.created(URI.create("/users/%s/role-mappings".formatted(id))).build();
        }
    }

    @DeleteMapping("/{id}/role-mappings")
    public ResponseEntity<?> deleteUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody String[] roleNamesToRemove)
    {
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            String clientUUID = clientConfig.getClientUUID(keycloakClient);
            List<RoleRepresentation> roleMappings = keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).listAll();
            List<RoleRepresentation> roleMappingsToRemove = roleMappings.stream().filter(role -> Arrays.stream(roleNamesToRemove).toList().contains(role.getName())).toList();
            keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).remove(roleMappingsToRemove);
            return ResponseEntity.noContent().build();
        }
    }

    @Value("${keycloak.realm}")
    private String realmName;

    private final ClientConfig clientConfig;

    private final KeycloakAdminClient keycloakAdminClient;
    private final KeycloakAuthClient keycloakAuthClient;
}
