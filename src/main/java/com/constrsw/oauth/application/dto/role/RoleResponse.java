package com.constrsw.oauth.application.dto.role;

public class RoleResponse {
    
    private String id;
    private String name;
    private String description;
    private boolean composite;
    
    public RoleResponse() {
    }
    
    public RoleResponse(String id, String name, String description, boolean composite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.composite = composite;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public boolean isComposite() {
        return composite;
    }
    
    public void setComposite(boolean composite) {
        this.composite = composite;
    }
}