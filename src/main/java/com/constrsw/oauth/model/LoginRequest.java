package com.constrsw.oauth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "O nome de usuário não pode estar em branco")
    @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_.@-]+$", message = "O nome de usuário deve conter apenas letras, números e os caracteres especiais: _ . @ -")
    private String username;

    @NotBlank(message = "A senha não pode estar em branco")
    @Size(min = 5, message = "A senha deve ter pelo menos 5 caracteres")
    private String password;
}