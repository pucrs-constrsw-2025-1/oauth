package com.grupo1.oauth.controller;

import com.grupo1.oauth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@RestControllerAdvice(basePackages = "com.grupo1.oauth.controller")
public class UserControllerAdvice {

    private static final String SOURCE = "OAuthAPI";
    private static final String MSG_BAD_REQUEST = "Invalid request.";
    private static final String MSG_UNAUTHORIZED = "Invalid credentials.";
    private static final String MSG_FORBIDDEN = "Token does not have permission.";
    private static final String MSG_NOT_FOUND = "Resource not found.";
    private static final String MSG_CONFLICT = "User already exists.";
    private static final String MSG_VALIDATION_ERROR = "Validation error.";
    private static final String MSG_KEYCLOAK_ERROR = "Error communicating with Keycloak.";
    private static final String MSG_UNEXPECTED_ERROR = "Unexpected error.";

    @ExceptionHandler(WebClientResponseException.BadRequest.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(WebClientResponseException.BadRequest ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse("400", MSG_BAD_REQUEST, ex));
    }

    @ExceptionHandler(WebClientResponseException.Unauthorized.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(WebClientResponseException.Unauthorized ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildErrorResponse("401", MSG_UNAUTHORIZED, ex));
    }

    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    public ResponseEntity<ErrorResponse> handleForbidden(WebClientResponseException.Forbidden ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse("403", MSG_FORBIDDEN, ex));
    }

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(WebClientResponseException.NotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse("404", MSG_NOT_FOUND, ex));
    }

    @ExceptionHandler(WebClientResponseException.Conflict.class)
    public ResponseEntity<ErrorResponse> handleConflict(WebClientResponseException.Conflict ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse("409", MSG_CONFLICT, ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse("400", MSG_VALIDATION_ERROR, ex));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleOtherWebClientErrors(WebClientResponseException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse("500", MSG_KEYCLOAK_ERROR, ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse("500", MSG_UNEXPECTED_ERROR, ex));
    }

    private ErrorResponse buildErrorResponse(String code, String description, Throwable e) {
        List<Map<String, Object>> stack = new ArrayList<>();
        Throwable current = e;

        while (current != null) {
            Map<String, Object> stackItem = new HashMap<>();
            stackItem.put("exception", current.getClass().getName());
            stackItem.put("message", current.getMessage());
            if (current.getCause() != null) {
                stackItem.put("cause", current.getCause().getClass().getName());
            }
            stack.add(stackItem);
            current = current.getCause();
        }

        return new ErrorResponse(code, description, SOURCE, stack);
    }
}
