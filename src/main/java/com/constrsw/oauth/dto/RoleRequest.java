package com.constrsw.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para requisição de criação/atualização de role
 */
@Data
public class RoleRequest {
    @NotBlank(message = "Nome da role é obrigatório")
    private String name;
    
    private String description;
}