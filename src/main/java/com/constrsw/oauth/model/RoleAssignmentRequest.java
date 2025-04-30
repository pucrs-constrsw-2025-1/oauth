package com.constrsw.oauth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleAssignmentRequest {
    @NotBlank(message = "O userId não pode ser vazio")
    private String userId;

    @NotBlank(message = "O roleName não pode ser vazio")
    private String roleName;
}