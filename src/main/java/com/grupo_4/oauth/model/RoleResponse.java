package com.grupo_4.oauth.model;

import lombok.Data;

@Data
public class RoleResponse {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    private boolean clientRole;
    private String containerId;
} 