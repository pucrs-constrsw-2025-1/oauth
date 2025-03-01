package org.firpy.keycloakwrapper.adapters.login;

import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.AccessTokenRequest;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.springframework.beans.factory.annotation.Value;
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
	    AccessTokenRequest tokenPasswordRequest = new AccessTokenRequest
		(
			clientId,
			clientSecret,
			request.username(),
			request.password(),
			"password"
		);

	    return keycloakClient.getAccessTokenWithPassword(tokenPasswordRequest);
    }

	@PostMapping("/refresh")
	AccessToken loginWithRefreshToken(RefreshTokenRequest request)
	{
		KeycloakRefreshTokenRequest keycloakRefreshTokenRequest = new KeycloakRefreshTokenRequest(clientId, request.refreshToken());
		return keycloakClient.getAccessTokenWithRefreshToken(keycloakRefreshTokenRequest);
	}

    private final KeycloakAuthClient keycloakClient;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${keycloak.client-secret}")
	private String clientSecret;
}
