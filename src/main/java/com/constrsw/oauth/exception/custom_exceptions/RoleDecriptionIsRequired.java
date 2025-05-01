package com.constrsw.oauth.exception.custom_exceptions;

public class RoleDecriptionIsRequired extends RuntimeException {
    public RoleDecriptionIsRequired() {
        super("A discrição da role é obrigatória");
    }
}

