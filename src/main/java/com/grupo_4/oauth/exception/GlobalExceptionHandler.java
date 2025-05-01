package com.grupo_4.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    private static final String ERROR_SOURCE = "OAuthAPI";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.error("Authentication error: {}", ex.getMessage());
        
        // Try to extract error code from Keycloak response if available
        String errorCode = "OA-401";
        if (ex.getCause() instanceof HttpClientErrorException httpEx) {
            errorCode = extractKeycloakErrorCode(httpEx);
        }
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(ex.getMessage());
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack 
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        log.error("Token refresh error: {}", ex.getMessage());
        
        // Try to extract error code from Keycloak response if available
        String errorCode = "OA-401";
        if (ex.getCause() instanceof HttpClientErrorException httpEx) {
            errorCode = extractKeycloakErrorCode(httpEx);
        }
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(ex.getMessage());
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<ErrorResponse> handleUserCreationException(UserCreationException ex, WebRequest request) {
        log.error("User creation error: {}", ex.getMessage());
        
        // Try to extract error code from Keycloak response if available
        String errorCode = "OA-400";
        if (ex.getCause() instanceof HttpClientErrorException httpEx) {
            errorCode = extractKeycloakErrorCode(httpEx);
        }
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(ex.getMessage());
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(UserFetchException.class)
    public ResponseEntity<ErrorResponse> handleUserFetchException(UserFetchException ex, WebRequest request) {
        log.error("User fetch error: {}", ex.getMessage());
        
        // Try to extract error code from Keycloak response if available
        String errorCode = "OA-400";
        if (ex.getCause() instanceof HttpClientErrorException httpEx) {
            errorCode = extractKeycloakErrorCode(httpEx);
        }
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(ex.getMessage());
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error("User not found: {}", ex.getMessage());
        
        String errorCode = "OA-404";
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(ex.getMessage());
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {
        log.error("HTTP client error: {}", ex.getMessage(), ex);
        
        String errorCode = extractKeycloakErrorCode(ex);
        String errorDescription = extractErrorDescription(ex);
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(errorDescription);
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Add stack entry
        errorResponse.addToErrorStack(errorCode, errorDescription, "Keycloak");
        
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }
    
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClientException(RestClientException ex, WebRequest request) {
        log.error("REST client error: {}", ex.getMessage(), ex);
        
        String errorCode = "OA-500";
        String errorDescription = "Failed to connect to authentication service: " + ex.getMessage();
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(errorDescription);
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, WebRequest request) {
        log.error("Missing parameter: {}", ex.getMessage(), ex);
        
        String errorCode = "OA-400";
        String errorDescription = "Missing required parameter: " + ex.getParameterName();
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(errorDescription);
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        String errorCode = "OA-500";
        String errorDescription = "An unexpected error occurred: " + ex.getMessage();
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(errorDescription);
        errorResponse.setErrorSource(ERROR_SOURCE);
        
        // Build error stack
        buildErrorStack(errorResponse, ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Build a complete error stack by traversing all nested exceptions
     */
    private void buildErrorStack(ErrorResponse errorResponse, Throwable throwable) {
        if (throwable == null) {
            return;
        }
        
        // Add current exception to stack
        String errorCode = determineErrorCode(throwable);
        errorResponse.addToErrorStack(errorCode, throwable.getMessage(), ERROR_SOURCE);
        
        // Process HttpClientErrorException specially to extract Keycloak error details
        if (throwable instanceof HttpClientErrorException httpEx) {
            try {
                String responseBody = httpEx.getResponseBodyAsString();
                if (responseBody != null && !responseBody.isEmpty()) {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    
                    // Add Keycloak error to stack
                    String keycloakError = rootNode.has("error") 
                        ? rootNode.get("error").asText() 
                        : errorCode;
                    
                    String keycloakErrorDesc = rootNode.has("error_description") 
                        ? rootNode.get("error_description").asText() 
                        : (rootNode.has("errorMessage") ? rootNode.get("errorMessage").asText() : responseBody);
                    
                    errorResponse.addToErrorStack(keycloakError, keycloakErrorDesc, "Keycloak");
                }
            } catch (Exception e) {
                log.warn("Failed to parse Keycloak error response", e);
            }
        }
        
        // Process cause recursively
        if (throwable.getCause() != null && throwable.getCause() != throwable) {
            buildErrorStack(errorResponse, throwable.getCause());
        }
    }
    
    /**
     * Determine appropriate error code based on exception type
     */
    private String determineErrorCode(Throwable throwable) {
        if (throwable instanceof AuthenticationException) {
            return "OA-401";
        } else if (throwable instanceof TokenRefreshException) {
            return "OA-401";
        } else if (throwable instanceof UserCreationException) {
            return "OA-400";
        } else if (throwable instanceof UserFetchException) {
            return "OA-400";
        } else if (throwable instanceof UserNotFoundException) {
            return "OA-404";
        } else if (throwable instanceof HttpClientErrorException httpEx) {
            return extractKeycloakErrorCode(httpEx);
        } else if (throwable instanceof RestClientException) {
            return "OA-500";
        } else if (throwable instanceof MissingServletRequestParameterException) {
            return "OA-400";
        } else {
            return "OA-500";
        }
    }
    
    /**
     * Extract error code from Keycloak error response
     */
    private String extractKeycloakErrorCode(HttpClientErrorException ex) {
        try {
            String responseBody = ex.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                // Try to get the error field which often contains the error code
                if (rootNode.has("error")) {
                    return rootNode.get("error").asText();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse Keycloak error response", e);
        }
        
        // Default error code based on HTTP status
        return "OA-" + ex.getStatusCode().value();
    }
    
    /**
     * Extract error description from Keycloak error response
     */
    private String extractErrorDescription(HttpClientErrorException ex) {
        try {
            String responseBody = ex.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                // Try to get the error_description field
                if (rootNode.has("error_description")) {
                    return rootNode.get("error_description").asText();
                }
                
                // Fallback to message field if available
                if (rootNode.has("message")) {
                    return rootNode.get("message").asText();
                }
                
                // Return the whole response if no specific field found
                return responseBody;
            }
        } catch (Exception e) {
            log.warn("Failed to parse Keycloak error response", e);
        }
        
        // Default to exception message
        return ex.getMessage();
    }
} 