package com.constrsw.oauth.application.dto.user;

import jakarta.validation.constraints.NotBlank;

public class PasswordRequest {
    
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    public PasswordRequest() {
    }
    
    public PasswordRequest(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}