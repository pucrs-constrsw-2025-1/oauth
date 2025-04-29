package com.constrsw.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
class ApiError {
    private String error_code;
    private String error_description;
    private String error_source;
    private List<String> error_stack;
    private LocalDateTime timestamp = LocalDateTime.now();
}

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KeycloakIntegrationException.class)
    public ResponseEntity<ApiError> handleClient(KeycloakIntegrationException ex) {
        var code = ex.getCause() instanceof NotFoundException ? 404 : 400;
        var err = new ApiError(
                "OA-000",
                ex.getMessage(),
                "OAuthAPI",
                Arrays.stream(ex.getStackTrace())
                        .map(Object::toString)
                        .toList(), null);
        return ResponseEntity.status(code).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        var err = new ApiError(
                "OA-999",
                "Erro interno. Contate o administrador.",
                "OAuthAPI",
                Arrays.stream(ex.getStackTrace())
                        .map(Object::toString)
                        .toList(), null);
        return ResponseEntity.status(500).body(err);
    }
}