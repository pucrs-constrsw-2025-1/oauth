package com.grupo_4.oauth.exception;

import lombok.Getter;

@Getter
public class TokenRefreshException extends RuntimeException {
    
    public TokenRefreshException(String message) {
        super(message);
    }
    
    public TokenRefreshException(String message, Throwable cause) {
        super(message, cause);
    }
} 