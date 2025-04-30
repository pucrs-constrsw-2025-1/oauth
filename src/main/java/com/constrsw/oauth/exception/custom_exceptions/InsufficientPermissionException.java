package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.ForbiddenException;

public class InsufficientPermissionException extends ForbiddenException {
    public InsufficientPermissionException() {
        super("Access token não concede permissão para acessar esse endpoint");
    }
}