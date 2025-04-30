package com.constrsw.oauth.exception.user_exceptions;

import jakarta.ws.rs.BadRequestException;

public class EmailEmptyException extends BadRequestException {
    public EmailEmptyException() {
        super("Email não pode ser vazio");
    }
}