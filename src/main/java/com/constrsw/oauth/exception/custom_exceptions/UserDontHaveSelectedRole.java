package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.BadRequestException;

public class UserDontHaveSelectedRole extends BadRequestException {
    public UserDontHaveSelectedRole() {
        super("O usuário nao possui essa role");
    }
}
