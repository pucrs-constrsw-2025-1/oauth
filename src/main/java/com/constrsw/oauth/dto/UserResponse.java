package com.constrsw.oauth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para resposta com dados de usuário
 */
@Data
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean enabled;
}