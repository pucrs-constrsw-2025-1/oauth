package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.BadRequestException;

public class UserIdRetrievalException extends BadRequestException {
    public UserIdRetrievalException() {
        super("Falha ao obter ID do usuário criado");
    }
}