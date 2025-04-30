package com.constrsw.oauth.exception;

import jakarta.ws.rs.*;
import org.keycloak.admin.client.resource.RoleResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_SOURCE = "OAuthAPI";

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        String errorCode = ex.getResponse() != null ? String.valueOf(ex.getResponse().getStatus()) : "OA-400";
        ErrorResponse errorResponse = buildErrorResponse(errorCode, ex.getMessage(), ERROR_SOURCE);
        addToErrorStack(errorResponse, errorCode, ex.getMessage(), ERROR_SOURCE);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        String errorCode = ex.getResponse() != null ? String.valueOf(ex.getResponse().getStatus()) : "OA-404";
        ErrorResponse errorResponse = buildErrorResponse(errorCode, ex.getMessage(), ERROR_SOURCE);
        addToErrorStack(errorResponse, errorCode, ex.getMessage(), ERROR_SOURCE);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        String errorCode = ex.getResponse() != null ? String.valueOf(ex.getResponse().getStatus()) : "OA-403";
        ErrorResponse errorResponse = buildErrorResponse(errorCode, ex.getMessage(), ERROR_SOURCE);
        addToErrorStack(errorResponse, errorCode, ex.getMessage(), ERROR_SOURCE);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleClientErrorException(ClientErrorException ex) {
        String errorCode = ex.getResponse() != null ? String.valueOf(ex.getResponse().getStatus()) : "OA-400";
        ErrorResponse errorResponse = buildErrorResponse(errorCode, ex.getMessage(), ERROR_SOURCE);
        addToErrorStack(errorResponse, errorCode, ex.getMessage(), ERROR_SOURCE);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getResponse().getStatus()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse errorResponse = buildErrorResponse("OA-401", ex.getMessage(), ERROR_SOURCE);
        addToErrorStack(errorResponse, "OA-401", ex.getMessage(), ERROR_SOURCE);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorResponse errorResponse = buildErrorResponse("OA-404", "Recurso não encontrado: " + ex.getRequestURL(), ERROR_SOURCE);
        addToErrorStack(errorResponse, "OA-404", ex.getMessage(), ERROR_SOURCE);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = buildErrorResponse("OA-500", "Erro interno do servidor", ERROR_SOURCE);
        addToErrorStack(errorResponse, "OA-500", ex.getMessage(), ERROR_SOURCE);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void handleKeycloakException(RuntimeException e, String resourceName) {
        if (e instanceof BadRequestException) {
            throw (BadRequestException) e;
        } else if (e instanceof NotFoundException) {
            throw new NotFoundException("Recurso " + resourceName + " não encontrado");
        } else if (e instanceof ForbiddenException) {
            throw (ForbiddenException) e;
        } else if (e instanceof ClientErrorException) {
            throw (ClientErrorException) e;
        } else {
            throw new InternalServerErrorException("Erro ao processar requisição para " + resourceName);
        }
    }

    private static ErrorResponse buildErrorResponse(String errorCode, String description, String source) {
        return ErrorResponse.builder()
                .error_code(errorCode)
                .error_description(description)
                .error_source(source)
                .error_stack(new ArrayList<>())
                .build();
    }

    private static void addToErrorStack(ErrorResponse errorResponse, String code, String message, String source) {
        ErrorResponse.StackError stackError = ErrorResponse.StackError.builder()
                .code(code)
                .message(message)
                .source(source)
                .build();
        errorResponse.getError_stack().add(stackError);
    }
}