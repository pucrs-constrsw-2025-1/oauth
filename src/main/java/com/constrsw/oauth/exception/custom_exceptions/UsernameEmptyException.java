package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.BadRequestException;

public class UsernameEmptyException extends BadRequestException {
    public UsernameEmptyException() {
        super("Username não pode ser vazio");
    }
}