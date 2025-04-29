package com.constrsw.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Requisição para atribuição ou remoção de roles a um usuário")
public class RoleAssignmentRequest {
    
    @NotEmpty(message = "A lista de IDs de roles não pode ser vazia")
    @Schema(
        description = "Lista de IDs das roles a serem atribuídas ou removidas do usuário",
        example = "[\"f47ac10b-58cc-4372-a567-0e02b2c3d479\", \"d93e8582-47b8-4e30-9c5f-aa34d6b9d46c\"]",
        required = true
    )
    private List<String> roleIds;
}