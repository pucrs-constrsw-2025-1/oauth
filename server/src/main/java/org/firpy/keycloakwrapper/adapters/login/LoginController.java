package org.firpy.keycloakwrapper.adapters.login;

import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.firpy.keycloakwrapper.utils.LoginUtils;
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
	public LoginController(KeycloakAuthClient keycloakClient, ClientConfig clientConfig)
	{
		this.keycloakClient = keycloakClient;
        this.clientConfig = clientConfig;
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
	    return keycloakClient.getAccessTokenWithPassword(LoginUtils.getLoginParameters(request, clientConfig.getAdminUsername(), clientConfig.getClientId()));
    }

	@PostMapping("/refresh")
	AccessToken loginWithRefreshToken(RefreshTokenRequest request)
	{
		return keycloakClient.getAccessTokenWithRefreshToken(LoginUtils.getRefreshParameters(request, clientConfig.getClientId()));
	}

    private final KeycloakAuthClient keycloakClient;
	private final ClientConfig clientConfig;
}
