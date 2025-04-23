package com.constrsw.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Nome de usuário é obrigatório")
    private String username;
    
    @NotBlank(message = "Senha é obrigatória")
    private String password;
}