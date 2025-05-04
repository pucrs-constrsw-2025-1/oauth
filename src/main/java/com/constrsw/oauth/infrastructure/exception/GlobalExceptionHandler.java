package com.constrsw.oauth.infrastructure.exception;

import com.constrsw.oauth.domain.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<GlobalException> handleDomainException(DomainException ex, WebRequest request) {
        log.error("Domain exception occurred: {}", ex.getMessage(), ex);
        
        GlobalException response = new GlobalException(
                ex.getCode(),
                ex.getMessage(),
                ex.getSource()
        );
        
        // Add the exception to stack
        GlobalException.ErrorStackItem stackItem = new GlobalException.ErrorStackItem(
                ex.getMessage(),
                ex.getSource(),
                getStackTraceAsString(ex)
        );
        response.addErrorStackItem(stackItem);
        
        // Add cause if present
        if (ex.getCause() != null) {
            GlobalException.ErrorStackItem causeItem = new GlobalException.ErrorStackItem(
                    ex.getCause().getMessage(),
                    "Underlying Cause",
                    getStackTraceAsString(ex.getCause())
            );
            response.addErrorStackItem(causeItem);
        }
        
        HttpStatus status = mapErrorCodeToStatus(ex.getCode());
        return new ResponseEntity<>(response, status);
    }
    
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<GlobalException> handleHttpClientErrorException(HttpClientErrorException ex) {
        log.error("HTTP client error: {}", ex.getMessage(), ex);
        
        String code = "OA-" + ex.getStatusCode().value();
        
        GlobalException response = new GlobalException(
                code,
                "External service error: " + ex.getMessage(),
                "KeycloakAPI"
        );
        
        GlobalException.ErrorStackItem stackItem = new GlobalException.ErrorStackItem(
                ex.getMessage(),
                "KeycloakAPI",
                getStackTraceAsString(ex)
        );
        response.addErrorStackItem(stackItem);
        
        return new ResponseEntity<>(response, ex.getStatusCode());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalException> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);
        
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));
        
        GlobalException response = new GlobalException(
                "OA-400",
                "Validation error: " + errorMessage,
                "OAuthAPI"
        );
        
        GlobalException.ErrorStackItem stackItem = new GlobalException.ErrorStackItem(
                errorMessage,
                "RequestValidation",
                getStackTraceAsString(ex)
        );
        response.addErrorStackItem(stackItem);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalException> handleAllExceptions(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        GlobalException response = new GlobalException(
                "OA-500",
                "Internal server error: " + ex.getMessage(),
                "OAuthAPI"
        );
        
        GlobalException.ErrorStackItem stackItem = new GlobalException.ErrorStackItem(
                ex.getMessage(),
                "UnexpectedException",
                getStackTraceAsString(ex)
        );
        response.addErrorStackItem(stackItem);
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private String getStackTraceAsString(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .limit(10) // Limit stack trace to first 10 elements
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
    
    private HttpStatus mapErrorCodeToStatus(String errorCode) {
        if (errorCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        if (errorCode.startsWith("OA-4")) {
            if (errorCode.equals("OA-401")) {
                return HttpStatus.UNAUTHORIZED;
            } else if (errorCode.equals("OA-403")) {
                return HttpStatus.FORBIDDEN;
            } else if (errorCode.equals("OA-404")) {
                return HttpStatus.NOT_FOUND;
            } else if (errorCode.equals("OA-409")) {
                return HttpStatus.CONFLICT;
            }
            return HttpStatus.BAD_REQUEST;
        } else if (errorCode.startsWith("OA-5")) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}