package com.constrsw.oauth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordUpdateRequest {
    @NotBlank(message = "A senha não pode ser vazia")
    private String password;
}
