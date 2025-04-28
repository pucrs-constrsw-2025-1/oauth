package org.firpy.oauth.adapters.users;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.core.Response;
import org.firpy.oauth.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.oauth.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.oauth.adapters.login.keycloak.auth.KeycloakUserInfo;
import org.firpy.oauth.errors.OAuthError;
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
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("users")
@SecurityRequirement(name = "bearerAuth")
public class UsersController
{
    public UsersController
            (
                    KeycloakAdminClient keycloakAdminClient,
                    KeycloakAuthClient keycloakAuthClient
            )
    {
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakAuthClient = keycloakAuthClient;
    }

    /**
     * Consumir a rota do Keycloak que recupera todos os usuários
     */
    @GetMapping()
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "200",
                                                    description = "Users retrieved",
                                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetUserResponse.class)))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "500",
                                                    description = "An unexpected error occurred",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> getUsers
    (
            @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled
    )
    {
        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            UsersResource usersResource = keycloakClient.realm(realmName).users();

            List<GetUserResponse> users = usersResource.search(
                    username,
                    firstName,
                    lastName,
                    email,
                    0, 10000,
                    enabled,
                    null
            ).stream().map(x -> new GetUserResponse(
                            x.getEmail(),
                            x.getFirstName(),
                            x.getLastName(),
                            x.isEnabled(),
                            x.getId()
                    )
            ).toList();

            return ResponseEntity.ok(users);
        }


    }

    /**
     * Consumir a rota do Keycloak que recupera um usuário a partir do seu id
     */
    @GetMapping("/{id}")
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "200",
                                                    description = "User retrieved",
                                                    content = @Content(schema = @Schema(implementation = GetUserResponse.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "400",
                                                    description = "Invalid request or user not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "404",
                                                    description = "User not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> getUser
    (
            @PathVariable("id") String id,
            @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken
    )
    {
        if (id == null || id.trim().isEmpty())
        {
            return ResponseEntity.badRequest().body(OAuthError.internalError("User ID is required"));
        }

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            UserRepresentation user = keycloakClient.realm(realmName).users().get(id).toRepresentation();

            return ResponseEntity.ok(new GetUserResponse(
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.isEnabled(),
                    user.getId()
            ));
        }


    }

    @GetMapping("/current")
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "200",
                                                    description = "User retrieved",
                                                    content = @Content(schema = @Schema(implementation = KeycloakUserInfo.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> getCurrentUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        return ResponseEntity.ok(keycloakAuthClient.getCurrentUser(accessToken));
    }

    /**
     * Consumir a rota do Keycloak que cria um novo usuário
     */
    @PostMapping()
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "201",
                                                    description = "User created",
                                                    content = @Content(schema = @Schema(implementation = CreateUserResponse.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "400",
                                                    description = "Invalid request or email",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "409",
                                                    description = "User already exists",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "500",
                                                    description = "An unexpected error occurred",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> createUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody CreateUserRequest createUserRequest)
    {

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

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
                    case 400 -> ResponseEntity.badRequest().body(OAuthError.keycloakError("Invalid request or email"));
                    case 409 ->
                            ResponseEntity.status(HttpStatus.CONFLICT).body(OAuthError.keycloakError("User already exists"));
                    case 401 ->
                            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.keycloakError("Invalid access token"));
                    case 403 ->
                            ResponseEntity.status(HttpStatus.FORBIDDEN).body(OAuthError.keycloakError("Access token lacks required admin scopes"));
                    default -> ResponseEntity.status(status).body(OAuthError.keycloakError("Unexpected error"));
                };
            }
        }
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PUT)
     */
    @PutMapping("/{id}")
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "204",
                                                    description = "User updated",
                                                    content = @Content(schema = @Schema())
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "400",
                                                    description = "Invalid request",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "404",
                                                    description = "User not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "500",
                                                    description = "An unexpected error occurred",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> updateUser
    (
            @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken,
            @PathVariable("id") String id,
            @RequestBody UpdateUserRequest updateUserRequest
    )
    {
        if (id == null || id.trim().isEmpty())
        {
            return ResponseEntity.badRequest().body(OAuthError.internalError("User ID is required"));
        }

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        if (updateUserRequest == null ||
                updateUserRequest.firstName() == null ||
                updateUserRequest.lastName() == null)
        {
            return ResponseEntity.badRequest().body("Invalid request body: missing required fields");
        }

        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            keycloakClient.realm(realmName).users().get(id).update(updateUserRequest.toKeycloakUserRepresentation());

            return ResponseEntity.noContent().build();
        }


    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PATCH)
     */
    @PatchMapping("/{id}")
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "204",
                                                    description = "User password updated",
                                                    content = @Content(schema = @Schema())
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "400",
                                                    description = "Invalid request or email",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "404",
                                                    description = "User not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "500",
                                                    description = "An unexpected error occurred",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> updateUserPassword(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody UpdateUserPasswordRequest passwordReset)
    {

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

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
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "200",
                                                    description = "User disabled successfully",
                                                    content = @Content(schema = @Schema(implementation = String.class, defaultValue = "User disabled successfully"))
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
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "404",
                                                    description = "User not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "500",
                                                    description = "Failed to disable user",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> deleteUser(@PathVariable String id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            UserResource userResource = keycloakClient.realm(realmName).users().get(id);
            UserRepresentation user = userResource.toRepresentation();

            user.setEnabled(false);
            userResource.update(user);

            return ResponseEntity.ok("User disabled successfully");
        }


    }

    @GetMapping("/{id}/role-mappings")
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "200",
                                                    description = "User role mappings retrieved",
                                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleRepresentation.class)))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "404",
                                                    description = "User not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "500",
                                                    description = "An unexpected error occurred",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> getUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id)
    {

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            MappingsRepresentation allRoles = keycloakClient.realm(realmName).users().get(id).roles().getAll();
            Map<String, ClientMappingsRepresentation> clientRoleMappings = allRoles.getClientMappings();
            List<RoleRepresentation> allRolesList = (clientRoleMappings != null ? clientRoleMappings.values().stream().flatMap(clientMappings -> clientMappings.getMappings().stream()).toList() : List.of());

            return ResponseEntity.ok(allRolesList.toArray(RoleRepresentation[]::new));
        }


    }

    @PostMapping("/{id}/role-mappings")
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "201",
                                                    description = "Role mappings created",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "400",
                                                    description = "Invalid request or email",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "404",
                                                    description = "User not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> createUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody String[] roleNamesToAdd)
    {

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            String clientUUID = keycloakClient.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
            List<RoleRepresentation> availableRoles = keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).listAvailable();
            List<RoleRepresentation> availableRolesToAdd = availableRoles.stream().filter(role -> Arrays.stream(roleNamesToAdd).toList().contains(role.getName())).toList();
            keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).add(availableRolesToAdd);

            return ResponseEntity.created(URI.create("/users/%s/role-mappings".formatted(id))).build();
        }


    }

    @DeleteMapping("/{id}/role-mappings")
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse
                                            (
                                                    responseCode = "204",
                                                    description = "Role mappings deleted"
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "400",
                                                    description = "Invalid request or email",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "401",
                                                    description = "Invalid access token",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "403",
                                                    description = "Access token lacks required admin scopes",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "404",
                                                    description = "User not found",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            ),
                                    @ApiResponse
                                            (
                                                    responseCode = "500",
                                                    description = "An unexpected error occurred",
                                                    content = @Content(schema = @Schema(implementation = OAuthError.class))
                                            )
                            }
            )
    public ResponseEntity<?> deleteUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody String[] roleNamesToRemove)
    {

        if (accessToken == null || accessToken.trim().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OAuthError.internalError("Access token is missing"));
        }

        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            String clientUUID = keycloakClient.realm(realmName).clients().findByClientId(clientId).getFirst().getId();
            List<RoleRepresentation> roleMappings = keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).listAll();
            List<RoleRepresentation> roleMappingsToRemove = roleMappings.stream().filter(role -> Arrays.stream(roleNamesToRemove).toList().contains(role.getName())).toList();
            keycloakClient.realm(realmName).users().get(id).roles().clientLevel(clientUUID).remove(roleMappingsToRemove);

            return ResponseEntity.noContent().build();
        }


    }

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.client-id}")
    private String clientId;

    private final KeycloakAdminClient keycloakAdminClient;
    private final KeycloakAuthClient keycloakAuthClient;
}
