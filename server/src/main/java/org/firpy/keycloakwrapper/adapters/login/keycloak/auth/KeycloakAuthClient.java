package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import org.firpy.keycloakwrapper.adapters.login.AccessToken;
import org.firpy.keycloakwrapper.adapters.login.KeycloakRefreshTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "keycloak-auth-service", url = "${keycloak.url}")
public interface KeycloakAuthClient
{
	@PostMapping("/auth/realms/${keycloak.realm}/protocol/openid-connect/token")
	AccessToken getAccessTokenWithPassword(AccessTokenRequest request);

	@PostMapping("/auth/realms/${keycloak.realm}/protocol/openid-connect/token")
	AccessToken getAccessTokenWithRefreshToken(KeycloakRefreshTokenRequest request);

	@GetMapping("auth/realms/${keycloak.realm}/protocol/openid-connect/userinfo")
	KeycloakUser getCurrentUser(@RequestHeader("Authorization") String accessToken);
}
