package org.firpy.oauth.utils;

import org.firpy.oauth.adapters.login.LoginRequest;
import org.firpy.oauth.adapters.login.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.ParseException;

@Component
public class LoginUtils {

	public MultiValueMap<String, ?> getLoginParameters(LoginRequest request)
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("username", request.email());
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("password", request.password());
        params.add("grant_type", "password");
        params.add("scope", "openid");

        return params;
    }

    public MultiValueMap<String, ?> getRefreshParameters(RefreshTokenRequest request) throws ParseException
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String token = request.refreshToken();

        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", token);
        params.add("scope", "openid");

        return params;
    }

    public MultiValueMap<String, Object> getIntrospectParameters(String accessToken)
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("token_type_hint", "requesting_party_token");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("token", accessToken);

        return params;
    }

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.client-id}")
    private String clientId;
}
