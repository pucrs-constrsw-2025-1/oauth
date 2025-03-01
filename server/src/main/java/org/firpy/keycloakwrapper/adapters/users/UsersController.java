package org.firpy.keycloakwrapper.adapters.users;

import org.firpy.keycloakwrapper.adapters.login.keycloak.admin.KeycloakAdminClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public KeycloakUser[] getUsers(@RequestHeader("Authorization") String accessToken)
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
    public KeycloakUser getUser(@PathVariable("id") String id, @RequestHeader("Authorization") String accessToken)
    {
	    return keycloakAdminClient.getUser(accessToken, id);
    }

    @GetMapping("/current")
    public KeycloakUser getCurrentUser(@RequestHeader("Authorization") String accessToken)
    {
        return keycloakClient.getCurrentUser(accessToken);
    }

    /**
     * Consumir a rota do Keycloak que cria um novo usuário
     * @param accessToken
     * @return
     */
    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestHeader("Authorization") String accessToken, @RequestBody KeycloakUser user)
    {
        return keycloakAdminClient.createUser(accessToken, user.toRequest());
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PUT)
     * @param accessToken
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, @RequestBody KeycloakUser user)
    {
        return keycloakAdminClient.updateUser(accessToken,id, user.toRequest());
    }

    /**
     * Consumir a rota do Keycloak que atualiza um usuário (método PATCH)
     * @param accessToken
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUserPassword(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, @RequestBody UpdateUserPasswordRequest request)
    {
        return keycloakAdminClient.resetPassword(accessToken, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, @RequestHeader("Authorization") String accessToken)
    {
        return keycloakAdminClient.deleteUser(accessToken, id);
    }

    private final KeycloakAuthClient keycloakClient;
    private final KeycloakAdminClient keycloakAdminClient;
}
