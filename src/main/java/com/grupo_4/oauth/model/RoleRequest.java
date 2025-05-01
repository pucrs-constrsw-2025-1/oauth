package com.grupo_4.oauth.model;

import lombok.Data;

@Data
public class RoleRequest {
    private String name;
    private String description;
    private Boolean composite;
    private Boolean clientRole;
    private String containerId;
} 