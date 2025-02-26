package org.firpy.keycloakwrapper.adapters.login.keycloak.admin;

import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakUser;
import org.firpy.keycloakwrapper.adapters.users.UpdateUserPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "keycloak-admin-service", url = "${keycloak.url}")
public interface KeycloakAdminClient
{
	@PostMapping("/auth/admin/realms/${keycloak.realm}/users")
	ResponseEntity<Void> createUser(@RequestHeader("Authorization") String accessToken, KeycloakUser user);

	@GetMapping("/auth/admin/realms/${keycloak.realm}/users")
	KeycloakUser[] getUsers(@RequestHeader("Authorization") String accessToken);

	@GetMapping("/auth/admin/realms/${keycloak.realm}/users/{id}")
	KeycloakUser getUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id);

	@PutMapping("/auth/admin/realms/${keycloak.realm}/users/{id}")
	ResponseEntity<Void> updateUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, KeycloakUser user);

	@PutMapping("auth/admin/realms/${keycloak.realm}/users/{id}/reset-password")
	ResponseEntity<Void> resetPassword(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, @RequestBody UpdateUserPasswordRequest request);

	@DeleteMapping("auth/admin/realms/${keycloak.realm}/users/{id}")
	ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id);
}
