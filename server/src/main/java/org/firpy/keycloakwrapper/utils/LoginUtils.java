package org.firpy.keycloakwrapper.utils;

import com.nimbusds.jwt.SignedJWT;
import org.firpy.keycloakwrapper.adapters.login.LoginRequest;
import org.firpy.keycloakwrapper.adapters.login.RefreshTokenRequest;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.ParseException;

@Component
public class LoginUtils {

	public LoginUtils(ClientConfig clientConfig)
	{
		this.clientConfig = clientConfig;
	}

	public MultiValueMap<String, ?> getLoginParameters(LoginRequest request)
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("client_id", request.username().equals(clientConfig.getAdminUsername()) ? clientConfig.getAdminClientId() : clientConfig.getClientId());
        params.add("client_secret", request.username().equals(clientConfig.getAdminUsername()) ? null : clientConfig.getClientSecret());
        params.add("username", request.username());
        params.add("password", request.password());
        params.add("grant_type", "password");
        params.add("scope", "openid");

        return params;
    }

    public MultiValueMap<String, ?> getRefreshParameters(RefreshTokenRequest request) throws ParseException
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        SignedJWT jwt = SignedJWT.parse(request.refreshToken());

        //Authorized party
        String azp = jwt.getJWTClaimsSet().getStringClaim("azp");
        boolean isAdmin = azp.equals(clientConfig.getAdminClientId());

        params.add("client_id", isAdmin ? clientConfig.getAdminClientId() : clientConfig.getClientId());
        params.add("client_secret", isAdmin ? null : clientConfig.getClientSecret());
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", request.refreshToken());
        params.add("scope", "openid");

        return params;
    }

    private final ClientConfig clientConfig;
}
