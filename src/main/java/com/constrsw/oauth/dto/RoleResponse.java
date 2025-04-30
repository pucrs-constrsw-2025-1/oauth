package com.constrsw.oauth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para resposta com dados de role
 */
@Data
@Builder
public class RoleResponse {
    private String id;
    private String name;
    private String description;
    private Boolean composite;
    private Boolean clientRole;
    private String containerId;
}