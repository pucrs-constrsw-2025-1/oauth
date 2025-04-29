package com.constrsw.oauth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    private String id;
    private String name;
    private String description;
    private Boolean composite;
    private Boolean clientRole;
    private String containerId;
}package com.constrsw.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Resposta contendo informações de uma role")
public class RoleResponse {
    
    @Schema(description = "ID único da role", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private String id;
    
    @Schema(description = "Nome da role", example = "admin")
    private String name;
    
    @Schema(description = "Descrição da role", example = "Administrador do sistema com acesso total")
    private String description;
    
    @Schema(description = "Indica se a role é composta por outras roles", example = "false")
    private Boolean composite;
    
    @Schema(description = "Indica se a role pertence a um cliente específico", example = "false")
    private Boolean clientRole;
    
    @Schema(description = "ID do container ao qual a role pertence", example = "constrsw")
    private String containerId;
}