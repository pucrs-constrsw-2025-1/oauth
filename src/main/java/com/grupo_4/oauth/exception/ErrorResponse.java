package com.grupo_4.oauth.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(
    String status,
    int statusCode,
    String message,
    String path,
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp
) {
    public ErrorResponse(String status, int statusCode, String message, String path) {
        this(status, statusCode, message, path, LocalDateTime.now());
    }
} 