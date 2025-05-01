package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.BadRequestException;

public class UserAlreadyHaveRole extends BadRequestException {
    public UserAlreadyHaveRole() {
        super("O usuário ja possui a role.");
    }
}
