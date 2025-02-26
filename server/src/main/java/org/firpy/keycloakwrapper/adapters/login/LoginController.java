package org.firpy.keycloakwrapper.adapters.login;

import org.firpy.keycloakwrapper.adapters.login.keycloak_adapter.AccessToken;
import org.firpy.keycloakwrapper.adapters.login.keycloak_adapter.AccessTokenRequest;
import org.firpy.keycloakwrapper.adapters.login.keycloak_adapter.KeycloakClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("login")
public class LoginController
{
	public LoginController(KeycloakClient keycloakClient)
	{
		this.keycloakClient = keycloakClient;
	}

	/**
     * Consumir a rota POST {{base-keycloak-url}}/auth/realms/{{realm}}/protocol/openid-connect/token da REST API
     * do Keycloak para autenticação de usuário, gerando o access_token e o refresh_token a partir do
     * client_id,
     * client_secret,
     * username,
     * password,
     * grant_type: password.
     * @param loginRequest
     * @return
     */
    @PostMapping()
    public LoginResponse login(@RequestBody LoginRequest loginRequest)
    {
	    AccessTokenRequest request = new AccessTokenRequest
		(
			loginRequest.clientId(),
			clientSecret,
			loginRequest.username(),
			loginRequest.password(),
	"password"
		);

	    AccessToken accessToken = keycloakClient.getAccessToken(request);
        return new LoginResponse
		(
			accessToken.tokenType(),
			accessToken.accessToken(),
			accessToken.expiresIn(),
			accessToken.refreshToken(),
			accessToken.refreshExpiresIn()
		);
    }

    private final KeycloakClient keycloakClient;

	@Value("${keycloak.client-secret}")
	private String clientSecret;
}
