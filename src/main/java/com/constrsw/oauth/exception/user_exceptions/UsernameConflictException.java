package com.constrsw.oauth.exception.user_exceptions;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class UsernameConflictException extends ClientErrorException {
    public UsernameConflictException(String username) {
        super("Usuário com username '" + username + "' já existe", Response.Status.CONFLICT);
    }
}