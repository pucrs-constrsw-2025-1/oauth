package com.constrsw.oauth.exception.user_exceptions;

import jakarta.ws.rs.BadRequestException;

public class NullUserDataException extends BadRequestException {
    public NullUserDataException() {
        super("Dados do usuário não podem ser nulos");
    }
}