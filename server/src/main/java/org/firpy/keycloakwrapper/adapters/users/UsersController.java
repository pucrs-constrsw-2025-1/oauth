package org.firpy.keycloakwrapper.adapters.users;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.core.Response;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakOIDCClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUserInfo;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.firpy.keycloakwrapper.utils.WebApplicationResponseUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.firpy.keycloakwrapper.utils.WebApplicationResponseUtils.toSpringResponseEntity;

@RestController()
@RequestMapping("users")
public class UsersController
{
	public UsersController(ClientConfig clientConfig, KeycloakAdminClient keycloakAdminClient, KeycloakOIDCClient keycloakOIDCClient)
	{
		this.clientConfig = clientConfig;
		this.keycloakAdminClient = keycloakAdminClient;
		this.keycloakOIDCClient = keycloakOIDCClient;
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
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserRepresentation.class)))),
        @ApiResponse(
            responseCode = "500",
            description = "An unexpected error occurred",
            content = @Content(schema = @Schema(implementation = String.class)))
        }
    )
    public ResponseEntity<?> getUsers(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
        try (Keycloak keycloakClient = this.keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            UsersResource users = keycloakClient.realm(realmName).users();
            return ResponseEntity.ok(users.list().toArray(UserRepresentation[]::new));
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
        return keycloakOIDCClient.getCurrentUser(accessToken);
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
                return WebApplicationResponseUtils.toSpringResponseEntity(response);
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
        try (Keycloak keycloakClient = keycloakAdminClient.fromAdminAccessToken(accessToken))
        {
            try (Response response = keycloakClient.realm(realmName).users().delete(id))
            {
                return WebApplicationResponseUtils.toSpringResponseEntity(response);
            }
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
    private final KeycloakOIDCClient keycloakOIDCClient;
}
