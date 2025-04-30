package com.constrsw.oauth.exception;

import com.constrsw.oauth.exception.user_exceptions.InsufficientPermissionException;
import com.constrsw.oauth.exception.user_exceptions.InvalidAccessTokenException;
import jakarta.ws.rs.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(NotAuthorizedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<Object> handleConflictException(ClientErrorException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Endpoint não encontrado: " + ex.getRequestURL());
        body.put("método", ex.getHttpMethod());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Ocorreu um erro interno. Por favor, contate o administrador.");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void handleKeycloakException(RuntimeException e, String resourceName) {
        if (e instanceof WebApplicationException webEx) {
            Response response = webEx.getResponse();
            int status = response.getStatus();

            if (status == Response.Status.BAD_REQUEST.getStatusCode()) {
                throw new BadRequestException("Erro na estrutura da chamada.");
            }
            if (status == Response.Status.UNAUTHORIZED.getStatusCode()) {
                throw new InvalidAccessTokenException();
            }
            if (status == Response.Status.FORBIDDEN.getStatusCode()) {
                throw new InsufficientPermissionException();
            }
        }

        throw new BadRequestException("Erro desconhecido ao processar " + resourceName + ": " + e.getMessage());
    }
}