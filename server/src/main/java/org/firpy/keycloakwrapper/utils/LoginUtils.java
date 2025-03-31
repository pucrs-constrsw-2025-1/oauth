package org.firpy.keycloakwrapper.utils;

import com.nimbusds.jwt.SignedJWT;
import org.firpy.keycloakwrapper.adapters.login.LoginRequest;
import org.firpy.keycloakwrapper.adapters.login.RefreshTokenRequest;
import org.firpy.keycloakwrapper.setup.ClientConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.text.ParseException;

@Component
public class LoginUtils {

	public LoginUtils(ClientConfig clientConfig)
	{
		this.clientConfig = clientConfig;
	}

	public MultiValueMap<String, ?> getLoginParameters(LoginRequest request) throws IOException
	{
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("client_id", request.username().equals(clientConfig.getAdminUsername()) ? clientConfig.getAdminClientId() : clientConfig.getClientId());
        if (!request.username().equals(clientConfig.getAdminUsername()))
        {
            params.add("client_secret", clientConfig.getClientSecret());
        }

        params.add("username", request.username());
        params.add("password", request.password());
        params.add("grant_type", "password");
        params.add("scope", "openid");

        return params;
    }

    public MultiValueMap<String, ?> getRefreshParameters(RefreshTokenRequest request) throws ParseException, IOException
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String token = request.refreshToken();
        boolean isAdmin = isAdmin(token);

        params.add("client_id", isAdmin ? clientConfig.getAdminClientId() : clientConfig.getClientId());
        if (!isAdmin)
        {
            params.add("client_secret", clientConfig.getClientSecret());
        }

        params.add("grant_type", "refresh_token");
        params.add("refresh_token", token);
        params.add("scope", "openid");

        return params;
    }

    private boolean isAdmin(String token) throws ParseException
    {
        SignedJWT jwt = SignedJWT.parse(token);

        String authorizedParty = jwt.getJWTClaimsSet().getStringClaim("azp");
	    return authorizedParty.equals(clientConfig.getAdminClientId());
    }

    public MultiValueMap<String, Object> getIntrospectParameters(String accessToken) throws IOException
    {
        //Introspect doesn't work on confidential clients (admin-cli)
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("token_type_hint", "requesting_party_token");
        params.add("client_secret", clientConfig.getClientSecret());
        params.add("client_id", clientConfig.getClientId());
        params.add("token", accessToken);
        return params;
    }

    private final ClientConfig clientConfig;
}
