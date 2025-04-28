package com.grupo1.oauth.dto;

import lombok.Data;

@Data
public class RoleResponse {
    private String id;
    private String name;
    private String description;
    private boolean enabled;
}
