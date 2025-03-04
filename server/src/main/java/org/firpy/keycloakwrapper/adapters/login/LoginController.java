package org.firpy.keycloakwrapper.adapters.login;

import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.utils.LoginUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("login")
public class LoginController
{
	public LoginController(KeycloakAuthClient keycloakClient, LoginUtils loginUtils)
	{
		this.keycloakClient = keycloakClient;
		this.loginUtils = loginUtils;
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
	    return keycloakClient.getAccessTokenWithPassword(loginUtils.getLoginParameters(request));
    }

	@PostMapping("/refresh")
	AccessToken loginWithRefreshToken(RefreshTokenRequest request)
	{
		return keycloakClient.getAccessTokenWithRefreshToken(loginUtils.getRefreshParameters(request));
	}

    private final KeycloakAuthClient keycloakClient;
	private final LoginUtils loginUtils;
}
