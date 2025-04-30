package com.constrsw.oauth.exception.custom_exceptions;

import jakarta.ws.rs.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(String roleId) {
        super("A Role com o ID: " + roleId + " não foi encontrada");
    }
}
