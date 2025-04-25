package com.grupo1.oauth.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data for authentication in Keycloak")
@Data
public class LoginRequest {

    @Schema(description = "Username (e-mail)", example = "admin@pucrs.br", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String username;

    @Schema(description = "User password", example = "a12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;
}
