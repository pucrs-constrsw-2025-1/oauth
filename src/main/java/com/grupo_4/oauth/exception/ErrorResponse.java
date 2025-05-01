package com.grupo_4.oauth.exception;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @JsonProperty("error_code")
    private String errorCode;
    
    @JsonProperty("error_description")
    private String errorDescription;
    
    @JsonProperty("error_source")
    private String errorSource;
    
    @JsonProperty("error_stack")
    private List<ErrorStackEntry> errorStack = new ArrayList<>();
    
    public void addToErrorStack(ErrorStackEntry entry) {
        if (this.errorStack == null) {
            this.errorStack = new ArrayList<>();
        }
        this.errorStack.add(entry);
    }
    
    public void addToErrorStack(String errorCode, String errorDescription, String errorSource) {
        addToErrorStack(new ErrorStackEntry(errorCode, errorDescription, errorSource));
    }
} 