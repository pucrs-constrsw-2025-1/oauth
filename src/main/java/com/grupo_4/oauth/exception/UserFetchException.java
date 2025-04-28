package com.grupo_4.oauth.exception;

public class UserFetchException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public UserFetchException(String message) {
        super(message);
    }

    public UserFetchException(String message, Throwable cause) {
        super(message, cause);
    }
} 