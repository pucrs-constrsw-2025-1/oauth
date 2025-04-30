package com.constrsw.oauth.exception.user_exceptions;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class GenericConflictException extends ClientErrorException {
    public GenericConflictException() {
        super("Conflito ao criar usuário: username já existe", Response.Status.CONFLICT);
    }
}