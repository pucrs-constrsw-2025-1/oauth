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
}
