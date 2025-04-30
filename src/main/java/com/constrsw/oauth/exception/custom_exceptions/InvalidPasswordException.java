package com.constrsw.oauth.exception.custom_exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidPasswordException extends AuthenticationException {
    public InvalidPasswordException() {
        super("A senha fornecida está incorreta");
    }
}
