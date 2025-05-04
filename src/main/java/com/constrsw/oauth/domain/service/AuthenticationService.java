package com.constrsw.oauth.domain.service;

import java.util.Map;

public interface AuthenticationService {
    
    Map<String, Object> authenticate(String username, String password);
    
    Map<String, Object> refreshToken(String refreshToken);
    
    void logout(String refreshToken);
    
    boolean validateToken(String token);
}