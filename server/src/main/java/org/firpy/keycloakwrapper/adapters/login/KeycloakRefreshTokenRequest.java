package org.firpy.keycloakwrapper.adapters.login;

public class KeycloakRefreshTokenRequest
{
	public KeycloakRefreshTokenRequest(String clientId, String refreshToken)
	{
		this.clientId = clientId;
		this.refreshToken = refreshToken;
	}

	private final String clientId;
	private final String refreshToken;
	private final String grantType = "refresh_token";
}
