package com.constrsw.oauth.exception.user_exceptions;

import jakarta.ws.rs.NotAuthorizedException;

public class InvalidAccessTokenException extends NotAuthorizedException {
    public InvalidAccessTokenException() {
        super("Access token inválido");
    }
}