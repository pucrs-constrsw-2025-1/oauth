package org.firpy.keycloakwrapper.adapters.login;

import io.swagger.v3.oas.annotations.media.Schema;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.IntrospectionResponse;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.utils.LoginUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

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

	@PostMapping("/introspect")
	public IntrospectionResponse introspectToken(@Schema(hidden = true) @RequestHeader("Authorization") String accessToken, String accessTokenToInspect)
	{
		return keycloakClient.introspectToken(accessToken, LoginUtils.getIntrospectParameters(accessTokenToInspect));
	}

	@PostMapping("/refresh")
	AccessToken loginWithRefreshToken(RefreshTokenRequest request) throws ParseException
	{
		return keycloakClient.getAccessTokenWithRefreshToken(loginUtils.getRefreshParameters(request));
	}

    private final KeycloakAuthClient keycloakClient;
	private final LoginUtils loginUtils;
}
