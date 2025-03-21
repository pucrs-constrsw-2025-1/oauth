package org.firpy.keycloakwrapper.adapters.users;

import io.swagger.v3.oas.annotations.media.Schema;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.RoleRepresentation;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUser;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController()
@RequestMapping("users")
public class UsersController
{
	public UsersController(KeycloakAuthClient keycloakClient, KeycloakAdminClient keycloakAdminClient)
	{
		this.keycloakClient = keycloakClient;
		this.keycloakAdminClient = keycloakAdminClient;
	}

	/**
     * Consumir a rota do Keycloak que recupera todos os usuários
     * @param accessToken
     * @return
     */
    @GetMapping()
    public KeycloakUser[] getUsers(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
        return keycloakAdminClient.getUsers(accessToken);
    }

    /**
     * Consumir a rota do Keycloak que recupera um usuário a partir do seu id
     * @param id
     * @param accessToken
     * @return
     */
    @GetMapping("/{id}")
    public KeycloakUser getUser(@PathVariable("id") String id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
	    return keycloakAdminClient.getUser(accessToken, id);
    }

    @GetMapping("/current")
    public KeycloakUserInfo getCurrentUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
        return keycloakClient.getCurrentUser(accessToken);
    }

    /**
     * Consumir a rota do Keycloak que cria um novo usuário
     * @param accessToken
     * @return
     */
    @PostMapping()
    public ResponseEntity<Void> createUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody CreateUserRequest createKeycloakUserRequest)
    {
        return keycloakAdminClient.createUser(accessToken, createKeycloakUserRequest.toCreateKeycloakUserRequest());
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PUT)
     * @param accessToken
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody UpdateUserRequest updateUserRequest)
    {
        return keycloakAdminClient.updateUser(accessToken,id, updateUserRequest.toUpdateKeycloakUserRequest());
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PATCH)
     * @param accessToken
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUserPassword(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody UpdateUserPasswordRequest passwordReset)
    {
        CredentialRequest credentialRequest = new CredentialRequest(passwordReset.newPassword());
        return keycloakAdminClient.resetPassword(accessToken, id, credentialRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken)
    {
        return keycloakAdminClient.deleteUser(accessToken, id);
    }

    @GetMapping("/{id}/role-mappings")
    public RoleRepresentation[] getUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id)
    {
        return keycloakAdminClient.getUserRoleMappings(accessToken, id);
    }

    @PostMapping("/{id}/role-mappings")
    public ResponseEntity<Void> createUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody RoleRepresentation[] roleMappings)
    {
        keycloakAdminClient.createUserRoleMappings(accessToken, id, roleMappings);
        return ResponseEntity.created(URI.create("/users/%s/role-mappings".formatted(id)))
                            .build();
    }

    @DeleteMapping("/{id}/role-mappings")
    public ResponseEntity<Void> deleteUserRoleMappings(@Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String accessToken, @PathVariable("id") String id, @RequestBody RoleRepresentation roleMapping)
    {
        keycloakAdminClient.deleteUserRoleMappings(accessToken, id, roleMapping);
        return ResponseEntity.noContent().build();
    }

    private final KeycloakAuthClient keycloakClient;
    private final KeycloakAdminClient keycloakAdminClient;
}
