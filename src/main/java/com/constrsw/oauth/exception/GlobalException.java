package com.constrsw.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalException extends RuntimeException {
    private final HttpStatus status;
    private final String errorSource;

    public GlobalException(String message, HttpStatus status, String errorSource) {
        super(message);
        this.status = status;
        this.errorSource = errorSource;
    }
}