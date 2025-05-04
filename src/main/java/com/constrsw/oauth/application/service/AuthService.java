package com.constrsw.oauth.application.service;

import com.constrsw.oauth.application.dto.auth.AuthRequest;
import com.constrsw.oauth.application.dto.auth.AuthResponse;
import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    
    private final AuthenticationService authenticationService;
    
    @Autowired
    public AuthService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    public AuthResponse login(AuthRequest authRequest) {
        try {
            Map<String, Object> authResult = authenticationService.authenticate(
                    authRequest.getUsername(),
                    authRequest.getPassword()
            );
            
            return mapToAuthResponse(authResult);
        } catch (Exception e) {
            throw new DomainException("OA-001", "Authentication failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        try {
            Map<String, Object> authResult = authenticationService.refreshToken(refreshToken);
            return mapToAuthResponse(authResult);
        } catch (Exception e) {
            throw new DomainException("OA-002", "Token refresh failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void logout(String refreshToken) {
        try {
            authenticationService.logout(refreshToken);
        } catch (Exception e) {
            throw new DomainException("OA-003", "Logout failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    private AuthResponse mapToAuthResponse(Map<String, Object> authResult) {
        AuthResponse response = new AuthResponse();
        response.setTokenType((String) authResult.get("token_type"));
        response.setAccessToken((String) authResult.get("access_token"));
        response.setExpiresIn((Integer) authResult.get("expires_in"));
        response.setRefreshToken((String) authResult.get("refresh_token"));
        response.setRefreshExpiresIn((Integer) authResult.get("refresh_expires_in"));
        return response;
    }
}