package org.firpy.keycloakwrapper.adapters.login;

import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("login")
public class LoginController
{
	public LoginController(KeycloakAuthClient keycloakClient)
	{
		this.keycloakClient = keycloakClient;
	}

	/**
     * Consumir a rota POST {{base-keycloak-url}}/auth/realms/{{realm}}/protocol/openid-connect/token da REST API
     * do Keycloak para autenticação de usuário, gerando o access_token e o refresh_token a partir do
     * client_id,
     * client_secret,
     * username,
     * newPassword,
     * grant_type: newPassword.
     * @param request
     * @return
     */
    @PostMapping()
    public AccessToken login(@RequestBody LoginRequest request)
    {
		//MultiValueMap otherwise spring cloud feign can't serialize it to www-form-urlencoded
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		//admin-cli is keycloak's default client for admin operations
		params.add("client_id", request.username().equals(adminUsername) ? "admin-cli" : clientId);
		params.add("username", request.username());
		params.add("password", request.password());
		params.add("grant_type", "password");


	    return keycloakClient.getAccessTokenWithPassword(params);
    }

	@PostMapping("/refresh")
	AccessToken loginWithRefreshToken(RefreshTokenRequest request)
	{
		//MultiValueMap otherwise spring cloud feign can't serialize it to www-form-urlencoded
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("client_id", clientId);
		params.add("grant_type", request.refreshToken());
		params.add("refresh_token", request.refreshToken());

		return keycloakClient.getAccessTokenWithRefreshToken(params);
	}

    private final KeycloakAuthClient keycloakClient;

	@Value("${keycloak.client-id}")
	private String clientId;

	private String clientSecret;

	@Value("${keycloak.admin-username}")
	private String adminUsername;
}
