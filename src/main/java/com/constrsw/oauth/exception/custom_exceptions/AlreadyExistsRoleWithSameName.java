package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.BadRequestException;

public class AlreadyExistsRoleWithSameName extends BadRequestException {
    public AlreadyExistsRoleWithSameName() {
        super("Já existe uma role com esse nome");
    }
}
