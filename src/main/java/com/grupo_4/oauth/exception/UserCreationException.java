package com.grupo_4.oauth.exception;

public class UserCreationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
} 