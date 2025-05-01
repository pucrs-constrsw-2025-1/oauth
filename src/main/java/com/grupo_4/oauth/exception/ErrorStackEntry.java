package com.grupo_4.oauth.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorStackEntry {
    private String errorCode;
    private String errorDescription;
    private String errorSource;
    private Throwable throwable;
    
    // Constructor without throwable for cleaner JSON serialization
    public ErrorStackEntry(String errorCode, String errorDescription, String errorSource) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.errorSource = errorSource;
    }
} 