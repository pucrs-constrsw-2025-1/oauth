package org.firpy.keycloakwrapper.adapters.login;

import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.IntrospectionResponse;
import org.firpy.keycloakwrapper.adapters.login.keycloak.auth.KeycloakAuthClient;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.firpy.keycloakwrapper.utils.LoginUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;

@RestController()
@RequestMapping("login")
public class AuthenticationController
{
	public AuthenticationController(KeycloakAuthClient keycloakClient, LoginUtils loginUtils, ClientConfig clientConfig)
	{
		this.keycloakAuthClient = keycloakClient;
		this.loginUtils = loginUtils;
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
    public AccessToken login(@RequestBody LoginRequest request) throws IOException
    {
	    return keycloakAuthClient.getAccessTokenWithPassword(loginUtils.getLoginParameters(request), realmName);
    }

	@PostMapping("/introspect")
	public IntrospectionResponse introspectToken(String accessTokenToInspect) throws IOException
	{
		byte[] basicAuthBytes = ("%s:%s".formatted(clientConfig.getClientId(), clientConfig.getClientSecret())).getBytes();
		return keycloakAuthClient.introspectToken("Basic %s".formatted(Base64.getEncoder().encodeToString(basicAuthBytes)), loginUtils.getIntrospectParameters(accessTokenToInspect));
	}

	@PostMapping("/refresh")
	AccessToken loginWithRefreshToken(RefreshTokenRequest request) throws ParseException, IOException
	{
		return keycloakAuthClient.getAccessTokenWithRefreshToken(loginUtils.getRefreshParameters(request));
	}

    private final KeycloakAuthClient keycloakAuthClient;
	private final LoginUtils loginUtils;
	private final ClientConfig clientConfig;

	@Value("${keycloak.realm}")
	private String realmName;
}
