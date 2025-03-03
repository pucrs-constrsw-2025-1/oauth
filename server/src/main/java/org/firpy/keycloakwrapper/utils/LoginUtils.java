package org.firpy.keycloakwrapper.utils;

import org.firpy.keycloakwrapper.adapters.login.LoginRequest;
import org.firpy.keycloakwrapper.adapters.login.RefreshTokenRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class LoginUtils {

    public static MultiValueMap<String, ?> getLoginParameters(LoginRequest request, String adminUsername, String clientId){
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("client_id", request.username().equals(adminUsername) ? "admin-cli" : clientId);
        params.add("username", request.username());
        params.add("password", request.password());
        params.add("grant_type", "password");

        return params;
    }

    public static MultiValueMap<String, ?> getRefreshParameters(RefreshTokenRequest request, String clientId){
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("client_id", clientId);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", request.refreshToken());

        return params;
    }
}
