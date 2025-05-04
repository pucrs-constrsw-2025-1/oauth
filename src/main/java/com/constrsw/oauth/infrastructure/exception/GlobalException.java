package com.constrsw.oauth.infrastructure.exception;

import java.util.ArrayList;
import java.util.List;

public class GlobalException {
    
    private String errorCode;
    private String errorDescription;
    private String errorSource;
    private List<ErrorStackItem> errorStack;
    
    public GlobalException() {
        this.errorStack = new ArrayList<>();
    }
    
    public GlobalException(String errorCode, String errorDescription, String errorSource) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.errorSource = errorSource;
        this.errorStack = new ArrayList<>();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorDescription() {
        return errorDescription;
    }
    
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    
    public String getErrorSource() {
        return errorSource;
    }
    
    public void setErrorSource(String errorSource) {
        this.errorSource = errorSource;
    }
    
    public List<ErrorStackItem> getErrorStack() {
        return errorStack;
    }
    
    public void setErrorStack(List<ErrorStackItem> errorStack) {
        this.errorStack = errorStack;
    }
    
    public void addErrorStackItem(ErrorStackItem item) {
        if (this.errorStack == null) {
            this.errorStack = new ArrayList<>();
        }
        this.errorStack.add(item);
    }
    
    public static class ErrorStackItem {
        private String message;
        private String source;
        private String trace;
        
        public ErrorStackItem() {
        }
        
        public ErrorStackItem(String message, String source, String trace) {
            this.message = message;
            this.source = source;
            this.trace = trace;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getSource() {
            return source;
        }
        
        public void setSource(String source) {
            this.source = source;
        }
        
        public String getTrace() {
            return trace;
        }
        
        public void setTrace(String trace) {
            this.trace = trace;
        }
    }
}