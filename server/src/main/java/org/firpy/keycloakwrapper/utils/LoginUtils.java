package org.firpy.keycloakwrapper.utils;

import lombok.NoArgsConstructor;
import org.firpy.keycloakwrapper.adapters.login.LoginRequest;
import org.firpy.keycloakwrapper.adapters.login.RefreshTokenRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LoginUtils {

    public static MultiValueMap<String, ?> getLoginParameters(LoginRequest request, String adminUsername, String clientId)
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("client_id", request.username().equals(adminUsername) ? "admin-cli" : clientId);
        params.add("username", request.username());
        params.add("password", request.password());
        params.add("grant_type", "password");
        params.add("scope", "openid");

        return params;
    }

    public static MultiValueMap<String, ?> getRefreshParameters(RefreshTokenRequest request, String clientId)
    {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("client_id", clientId);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", request.refreshToken());
        params.add("scope", "openid");

        return params;
    }
}
