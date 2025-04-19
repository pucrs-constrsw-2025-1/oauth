package com.constrsw.oauth.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private String errorCode;
    private String message;
    private String source;
    private String path;
    private String traceId; // For correlation with logs
    
    // Don't include stack trace in production responses
    @Builder.Default
    private boolean includeStackTrace = false;
    private String stackTrace; // Only populated if includeStackTrace is true
}