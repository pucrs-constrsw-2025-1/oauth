package org.firpy.keycloakwrapper.adapters.login.keycloak.auth;

import org.firpy.keycloakwrapper.adapters.login.AccessToken;
import org.firpy.keycloakwrapper.adapters.login.KeycloakRefreshTokenRequest;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

@FeignClient(name = "keycloak-auth-service", url = "${keycloak.url}", configuration = KeycloakAuthClient.Configuration.class)
public interface KeycloakAuthClient
{
	@PostMapping(value ="/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	AccessToken getAccessTokenWithPassword(@RequestBody MultiValueMap<String, ?> request);

	@PostMapping(value ="/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	AccessToken getAccessTokenWithRefreshToken(@RequestBody KeycloakRefreshTokenRequest request);

	@GetMapping("/realms/${keycloak.realm}/protocol/openid-connect/userinfo")
	KeycloakUser getCurrentUser(@RequestHeader("Authorization") String accessToken);


	class Configuration {
		@Bean
		Encoder formEncoder() {
			return new feign.form.FormEncoder();
		}
	}
}

