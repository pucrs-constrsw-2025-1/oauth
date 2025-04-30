package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String userId) {
        super("Usuário com o ID: " + userId + " não foi encontrado");
    }
}
