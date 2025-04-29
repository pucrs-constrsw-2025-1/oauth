package com.constrsw.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Resposta contendo informações de um usuário")
public class UserResponse {
    
    @Schema(description = "ID único do usuário", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private String id;
    
    @Schema(description = "Nome de usuário (e-mail)", example = "usuario@example.com")
    private String username;
    
    @Schema(description = "Nome do usuário", example = "João")
    private String firstName;
    
    @Schema(description = "Sobrenome do usuário", example = "Silva")
    private String lastName;
    
    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean enabled;
}