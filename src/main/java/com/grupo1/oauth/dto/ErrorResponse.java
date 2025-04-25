package com.grupo1.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Resposta de erro estruturada")
public class ErrorResponse {
    @Schema(example = "400", description = "Código de erro retornado pela API")
    public String error_code;

    @Schema(example = "Requisição inválida.", description = "Descrição do erro")
    public String error_description;

    @Schema(example = "OAuthAPI", description = "Fonte/origem do erro")
    public String error_source;

    @Schema(description = "Lista de detalhes da pilha de erro")
    public List<Map<String, Object>> error_stack;
}
