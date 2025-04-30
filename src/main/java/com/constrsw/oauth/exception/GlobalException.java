package com.constrsw.oauth.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

/**
 * Exceção global para erros da aplicação
 */
@Getter
public class GlobalException extends RuntimeException {
    private final String errorCode;
    private final String errorSource;
    private final HttpStatus httpStatus;

    public GlobalException(String errorCode, String message, String errorSource, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.errorSource = errorSource;
        this.httpStatus = httpStatus;
    }

    public GlobalException(String errorCode, String message, String errorSource, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorSource = errorSource;
        this.httpStatus = httpStatus;
    }
}