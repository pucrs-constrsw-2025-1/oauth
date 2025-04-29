package com.constrsw.oauth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDescriptionRequest {
    @NotBlank(message = "A descrição não pode ser vazia")
    private String description;
}
