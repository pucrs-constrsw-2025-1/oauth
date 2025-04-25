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

    @ExceptionHandler(WebClientResponseException.BadRequest.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(WebClientResponseException.BadRequest ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse("400", "Requisição inválida.", ex));
    }

    @ExceptionHandler(WebClientResponseException.Unauthorized.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(WebClientResponseException.Unauthorized ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildErrorResponse("401", "Credenciais inválidas.", ex));
    }

    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    public ResponseEntity<ErrorResponse> handleForbidden(WebClientResponseException.Forbidden ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse("403", "Token sem permissão.", ex));
    }

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(WebClientResponseException.NotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse("404", "Recurso não encontrado.", ex));
    }

    @ExceptionHandler(WebClientResponseException.Conflict.class)
    public ResponseEntity<ErrorResponse> handleConflict(WebClientResponseException.Conflict ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorResponse("409", "Usuário já existe.", ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse("400", "Erro de validação.", ex));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleOtherWebClientErrors(WebClientResponseException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse("500", "Erro de comunicação com Keycloak.", ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse("500", "Erro inesperado.", ex));
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
