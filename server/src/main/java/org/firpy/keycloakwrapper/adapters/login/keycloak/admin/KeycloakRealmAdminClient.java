package org.firpy.keycloakwrapper.adapters.login.keycloak.admin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "keycloak-realm-admin-service", url = "${keycloak.url}")
public interface KeycloakRealmAdminClient
{
	@PostMapping(value = "/admin/realms", consumes = MediaType.APPLICATION_JSON_VALUE)
	void createRealm(@RequestHeader("Authorization") String accessToken, @RequestBody String realmJson);
}
