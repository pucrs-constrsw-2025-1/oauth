package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import org.firpy.keycloakwrapper.adapters.login.AccessToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@FeignClient(name = "keycloak-auth-service", url = "${keycloak.url}", configuration = KeycloakAuthClient.Configuration.class)
public interface KeycloakAuthClient
{
	@PostMapping(value ="/realms/{realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	AccessToken getAccessTokenWithPassword(@RequestBody MultiValueMap<String, ?> request, @PathVariable("realm") String realm);

	@PostMapping(value ="/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	AccessToken getAccessTokenWithRefreshToken(@RequestBody MultiValueMap<String, ?> request);

	@GetMapping("/realms/${keycloak.realm}/protocol/openid-connect/userinfo")
	KeycloakUserInfo getCurrentUser(@RequestHeader("Authorization") String accessToken);

	@PostMapping(value ="/realms/${keycloak.realm}/protocol/openid-connect/token/introspect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	IntrospectionResponse introspectToken(@RequestHeader("Authorization") String authorization, @RequestBody MultiValueMap<String, ?> request);

	@PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	Map<String, Object> requestAuthorization(@RequestHeader("Authorization") String basicAuth, @RequestBody MultiValueMap<String, ?> request);

	class Configuration
	{
		@Bean
		Encoder formEncoder()
		{
			return new feign.form.FormEncoder();
		}
	}
}
