package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.NotAuthorizedException;

public class InvalidAccessTokenException extends NotAuthorizedException {
    public InvalidAccessTokenException() {
        super("Access token inválido");
    }
}