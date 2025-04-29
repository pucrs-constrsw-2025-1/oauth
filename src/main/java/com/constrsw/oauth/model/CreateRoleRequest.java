package com.constrsw.oauth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "O nome do role não pode ser vazio")
    private String name;
}

