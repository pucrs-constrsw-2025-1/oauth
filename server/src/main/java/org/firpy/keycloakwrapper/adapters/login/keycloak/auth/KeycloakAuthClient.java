package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import org.firpy.keycloakwrapper.adapters.login.AccessToken;
import org.firpy.keycloakwrapper.adapters.login.KeycloakRefreshTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "keycloak-auth-service", url = "${keycloak.url}")
public interface KeycloakAuthClient
{
	@PostMapping(value ="/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	AccessToken getAccessTokenWithPassword(@RequestBody AccessTokenRequest request);

	@PostMapping(value ="/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	AccessToken getAccessTokenWithRefreshToken(@RequestBody KeycloakRefreshTokenRequest request);

	@GetMapping("/realms/${keycloak.realm}/protocol/openid-connect/userinfo")
	KeycloakUser getCurrentUser(@RequestHeader("Authorization") String accessToken);
}
