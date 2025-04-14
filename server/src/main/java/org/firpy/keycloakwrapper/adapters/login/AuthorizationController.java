package org.firpy.keycloakwrapper.adapters.login;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	@ApiResponses(value = {
		@ApiResponse
		(
			responseCode = "200",
			description = "User is authorized to create resource"
		),
		@ApiResponse
		(
			responseCode = "403",
			description = "User is not authorized to create resource"
		),
		@ApiResponse
		(
			responseCode = "400",
			description = "Invalid resource"
		)
	})
	public ResponseEntity<Void> isAuthorizedToCreate(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@PatchMapping("/{resource}")
	@ApiResponses(value = {
			@ApiResponse
					(
							responseCode = "200",
							description = "User is authorized to patch resource"
					),
			@ApiResponse
					(
							responseCode = "403",
							description = "User is not authorized to patch resource"
					),
			@ApiResponse
					(
							responseCode = "400",
							description = "Invalid resource"
					)
	})
	public ResponseEntity<Void> isAuthorizedToPatch(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@PutMapping("/{resource}")
	@ApiResponses(value = {
			@ApiResponse
					(
							responseCode = "200",
							description = "User is authorized to put resource"
					),
			@ApiResponse
					(
							responseCode = "403",
							description = "User is not authorized to put resource"
					),
			@ApiResponse
					(
							responseCode = "400",
							description = "Invalid resource"
					)
	})
	public ResponseEntity<Void> isAuthorizedToPut(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@DeleteMapping("/{resource}")
	@ApiResponses(value = {
			@ApiResponse
					(
							responseCode = "200",
							description = "User is authorized to delete resource"
					),
			@ApiResponse
					(
							responseCode = "403",
							description = "User is not authorized to delete resource"
					),
			@ApiResponse
					(
							responseCode = "400",
							description = "Invalid resource"
					)
	})
	public ResponseEntity<Void> isAuthorizedToDelete(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "write") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	@GetMapping("/{resource}")
	@ApiResponses(value = {
			@ApiResponse
					(
							responseCode = "200",
							description = "User is authorized to get resource"
					),
			@ApiResponse
					(
							responseCode = "403",
							description = "User is not authorized to get resource"
					),
			@ApiResponse
					(
							responseCode = "400",
							description = "Invalid resource"
					)
	})
	public ResponseEntity<Void> isAuthorizedToGet(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource, "read") ? ResponseEntity.ok().build() : ResponseEntity.status(403).build();
	}

	private final AuthorizationService authorizationService;
}
