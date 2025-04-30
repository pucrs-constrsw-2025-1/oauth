package com.constrsw.oauth.exception.user_exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("A senha fornecida está incorreta");
    }
}