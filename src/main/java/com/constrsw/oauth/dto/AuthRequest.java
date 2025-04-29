package com.constrsw.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Username is required")
    @Schema(description = "Username for authentication", example = "user@example.com", required = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password for authentication", example = "password123", required = true)
    private String password;
}