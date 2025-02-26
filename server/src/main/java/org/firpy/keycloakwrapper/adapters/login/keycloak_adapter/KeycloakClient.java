package org.firpy.keycloakwrapper.adapters.login.keycloak_adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "keycloak-service", url = "${keycloak.url}")
public interface KeycloakClient
{
	@PostMapping("/auth/realms/${keycloak.realm}/protocol/openid-connect/token")
	AccessToken getAccessToken(AccessTokenRequest loginRequest);

	@GetMapping("auth/realms/${keycloak.realm}/protocol/openid-connect/userinfo")
	KeycloakUser getKeycloakUser(@RequestHeader("Authorization") String accessToken);
}
