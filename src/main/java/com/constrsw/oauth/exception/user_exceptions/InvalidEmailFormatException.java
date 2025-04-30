package com.constrsw.oauth.exception.user_exceptions;

import jakarta.ws.rs.BadRequestException;

public class InvalidEmailFormatException extends BadRequestException {
    public InvalidEmailFormatException() {
        super("Formato de email inválido");
    }
}