package org.firpy.oauth.errors;

import feign.FeignException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<OAuthError> handleNotAuthorized(NotAuthorizedException ex)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OAuthError.keycloakError("Invalid access token"));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<OAuthError> handleForbidden(ForbiddenException ex)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(OAuthError.keycloakError("Access token lacks required admin scopes"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<OAuthError> handleNotFound(NotFoundException ex)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(OAuthError.keycloakError("Resource not found"));
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<OAuthError> handleFeignNotFound(FeignException.NotFound ex)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(OAuthError.keycloakError("Resource not found"));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<OAuthError> handleBadRequest(BadRequestException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OAuthError.keycloakError("Invalid request or data"));
    }

    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<OAuthError> handleFeignBadRequest(FeignException.BadRequest ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OAuthError.keycloakError("Invalid request or data"));
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<OAuthError> handleFeignUnauthorized(FeignException.Unauthorized ex)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OAuthError.keycloakError("Invalid access token"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OAuthError> handleAllUncaught(Exception ex)
    {
        return ResponseEntity.internalServerError()
                .body(OAuthError.keycloakError("An unexpected error occurred: " + ex.getMessage()));
    }
}
