package org.firpy.keycloakwrapper.adapters;

import io.swagger.v3.oas.annotations.media.Schema;
import org.firpy.keycloakwrapper.services.AuthorizationService;
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
	public boolean isAuthorized(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, @PathVariable("resource") String resource)
	{
		return authorizationService.isAuthorized(accessToken, resource);
	}

	private final AuthorizationService authorizationService;
}
