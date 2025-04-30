package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.BadRequestException;

public class PasswordEmptyException extends BadRequestException {
    public PasswordEmptyException() {
        super("Senha não pode ser vazia");
    }
}