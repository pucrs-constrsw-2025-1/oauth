package com.grupo_4.oauth.config;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Configuration to disable Spring Boot's default error handling
 * so our custom GlobalExceptionHandler can handle all errors.
 */
@Configuration
public class ErrorHandlingConfig {

    /**
     * Override the default error attributes to prevent Spring Boot
     * from adding its own error details to the response.
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, 
                                                         org.springframework.boot.web.error.ErrorAttributeOptions options) {
                // Return empty map to disable Spring Boot's error attributes
                // Our GlobalExceptionHandler will handle the response format
                return Map.of();
            }
        };
    }
} 