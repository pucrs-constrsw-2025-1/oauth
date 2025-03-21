package org.firpy.keycloakwrapper.adapters.login.keycloak.admin;

import org.firpy.keycloakwrapper.adapters.clients.ClientRepresentation;
import org.firpy.keycloakwrapper.adapters.login.keycloak.CreateClientRequest;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUser;
import org.firpy.keycloakwrapper.adapters.users.CreateKeycloakUserRequest;
import org.firpy.keycloakwrapper.adapters.users.CredentialRequest;
import org.firpy.keycloakwrapper.adapters.users.UpdateKeycloakUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "keycloak-admin-service", url = "${keycloak.url}")
public interface KeycloakAdminClient
{
	@PostMapping("/admin/realms/${keycloak.realm}/users")
	ResponseEntity<Void> createUser(@RequestHeader("Authorization") String accessToken, CreateKeycloakUserRequest user);

	@GetMapping("/admin/realms/${keycloak.realm}/users")
	KeycloakUser[] getUsers(@RequestHeader("Authorization") String accessToken);

	@GetMapping("/admin/realms/${keycloak.realm}/users/{id}")
	KeycloakUser getUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id);

	@PutMapping("/admin/realms/${keycloak.realm}/users/{id}")
	ResponseEntity<Void> updateUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, UpdateKeycloakUserRequest user);

	@PutMapping("/admin/realms/${keycloak.realm}/users/{id}/reset-password")
	ResponseEntity<Void> resetPassword(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, @RequestBody CredentialRequest request);

	@DeleteMapping("/admin/realms/${keycloak.realm}/users/{id}")
	ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id);

	@PostMapping("/admin/realms/${keycloak.realm}/clients")
	ResponseEntity<Void> createClient(@RequestHeader("Authorization") String accessToken, @RequestBody CreateClientRequest request);

	@GetMapping("/admin/realms/${keycloak.realm}/clients")
	List<ClientRepresentation> getClients(@RequestHeader("Authorization") String accessToken);

	@PostMapping("/admin/realms/${keycloak.realm}/clients/{client-uuid}/client-secret")
	CredentialRequest createClientSecret(@RequestHeader("Authorization") String accessToken, @PathVariable("client-uuid") String clientUuid);

	@GetMapping("/admin/realms/${keycloak.realm}/clients/{client-uuid}/roles")
	RoleRepresentation[] getClientRoles(@RequestHeader("Authorization") String accessToken, @PathVariable("client-uuid") String clientUuid);

	@GetMapping("/admin/realms/${keycloak.realm}/clients/{client-uuid}/roles/{role-name}")
	RoleRepresentation getClientRole(@RequestHeader("Authorization") String accessToken, @PathVariable("client-uuid") String clientUuid, @PathVariable("role-name") String roleName);

	@PostMapping("/admin/realms/${keycloak.realm}/clients/{client-uuid}/roles")
	void createClientRole(@RequestHeader("Authorization") String accessToken, @PathVariable("client-uuid") String clientUuid, @RequestBody RoleRepresentation role);

	@DeleteMapping("/admin/realms/${keycloak.realm}/clients/{client-uuid}/roles/{role-name}")
	void deleteClientRole(@RequestHeader("Authorization") String accessToken, @PathVariable("client-uuid") String clientUuid, @PathVariable("role-name") String roleName);

	@PutMapping("/admin/realms/${keycloak.realm}/clients/{client-uuid}/roles/{role-name}")
	void updateClientRole(@RequestHeader("Authorization") String accessToken, @PathVariable("client-uuid") String clientUuid, @PathVariable("role-name") String roleName, @RequestBody RoleRepresentation role);

	@GetMapping("/admin/realms/${keycloak.realm}/roles")
	RoleRepresentation[] getRealmRoles(@RequestHeader("Authorization") String accessToken);

	@GetMapping("/admin/realms/${keycloak.realm}/roles/{role-name}")
	RoleRepresentation getRealmRole(@RequestHeader("Authorization") String accessToken, @PathVariable("role-name") String roleName);

	@PostMapping("/admin/realms/${keycloak.realm}/roles")
	void createRealmRole(@RequestHeader("Authorization") String accessToken, @RequestBody RoleRepresentation role);

	@DeleteMapping("/admin/realms/${keycloak.realm}/roles/{role-name}")
	void deleteRealmRole(@RequestHeader("Authorization") String accessToken, @PathVariable("role-name") String roleName);

	@PutMapping("/admin/realms/${keycloak.realm}/roles/{role-name}")
	void updateRealmRole(@RequestHeader("Authorization") String accessToken, @PathVariable("role-name") String roleName, @RequestBody RoleRepresentation role);

	@GetMapping("/admin/realms/${keycloak.realm}/users/{user-id}/role-mappings/realm")
	RoleRepresentation[] getUserRoleMappings(@RequestHeader("Authorization") String accessToken, @PathVariable("user-id") String userId);

	@PostMapping("/admin/realms/${keycloak.realm}/users/{user-id}/role-mappings/realm")
	void createUserRoleMappings(@RequestHeader("Authorization") String accessToken, @PathVariable("user-id") String userId, @RequestBody RoleRepresentation[] roleMapping);

	@DeleteMapping("/admin/realms/${keycloak.realm}/users/{user-id}/role-mappings/realm")
	void deleteUserRoleMappings(@RequestHeader("Authorization") String accessToken, @PathVariable("user-id") String userId, @RequestBody RoleRepresentation roleMapping);
}
