package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.BadRequestException;

public class RoleNameIsRequiredException extends BadRequestException {
    public RoleNameIsRequiredException() {
        super("O nome da role é obrigatório");
    }
}
