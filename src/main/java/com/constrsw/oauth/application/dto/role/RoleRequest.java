package com.constrsw.oauth.application.dto.role;

import jakarta.validation.constraints.NotBlank;

public class RoleRequest {
    
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    private String description;
    
    public RoleRequest() {
    }
    
    public RoleRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}