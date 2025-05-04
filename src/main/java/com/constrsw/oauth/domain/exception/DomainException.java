package com.constrsw.oauth.domain.exception;

public class DomainException extends RuntimeException {
    
    private final String code;
    private final String source;
    
    public DomainException(String code, String message, String source) {
        super(message);
        this.code = code;
        this.source = source;
    }
    
    public DomainException(String code, String message, String source, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.source = source;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getSource() {
        return source;
    }
}