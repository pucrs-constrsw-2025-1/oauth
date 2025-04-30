package com.constrsw.oauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para requisição de criação/atualização de usuário
 */
@Data
public class UserRequest {
    @NotBlank(message = "Username é obrigatório")
    @Email(message = "Email deve ser válido",
           regexp = "^[-!#-'+/-9=?A-Z^-~]+(\\.[-!#-'+/-9=?A-Z^-~]+)|\"([]!#-[^-~ \\t]|(\\\\[\\t -~]))+\")@([-!#-'+/-9=?A-Z^-~]+(\\.[-!#-'+/-9=?A-Z^-~]+)|\\[[\\t -Z^-~]*\\])")
    private String username;

    @NotBlank(message = "Password é obrigatório")
    private String password;

    @NotBlank(message = "First name é obrigatório")
    private String firstName;

    @NotBlank(message = "Last name é obrigatório")
    private String lastName;
}