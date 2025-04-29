package com.constrsw.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Requisição para criação ou atualização de role")
public class RoleRequest {
    
    @NotBlank(message = "Nome da role é obrigatório")
    @Schema(description = "Nome da role", example = "admin", required = true)
    private String name;
    
    @Schema(description = "Descrição da role", example = "Administrador do sistema com acesso total")
    private String description;
}