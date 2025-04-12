package org.firpy.keycloakwrapper.adapters;

import io.swagger.v3.oas.annotations.media.Schema;
import org.firpy.keycloakwrapper.services.AuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthorizationController
{
	public AuthorizationController(AuthorizationService authorizationService)
	{
		this.authorizationService = authorizationService;
	}

	@PostMapping("/{resource}")
	public ResponseEntity<Void> isAuthorizedToCreate(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@PatchMapping("/{resource}")
	public ResponseEntity<Void> isAuthorizedToPatch(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@PutMapping("/{resource}")
	public ResponseEntity<Void> isAuthorizedToPut(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@DeleteMapping("/{resource}")
	public ResponseEntity<Void> isAuthorizedToDelete(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@GetMapping("/{resource}")
	public ResponseEntity<Void> isAuthorizedToGet(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "read") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	private final AuthorizationService authorizationService;
}
