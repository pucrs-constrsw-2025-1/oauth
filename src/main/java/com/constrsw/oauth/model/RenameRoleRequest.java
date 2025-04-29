package com.constrsw.oauth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RenameRoleRequest {
    @NotBlank(message = "O novo nome não pode ser vazio")
    private String newName;
}
