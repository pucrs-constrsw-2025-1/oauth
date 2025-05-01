package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("Usuário não foi encontrado");
    }
}
