package com.constrsw.oauth.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "O username não pode ser nulo ou vazio")
    @Email(message = "O email deve ser um endereço válido")
    private String username;

    @NotBlank(message = "O primeiro nome não pode ser nulo ou vazio")
    private String firstName;

    @NotBlank(message = "O último nome não pode ser nulo ou vazio")
    private String lastName;

    @Size(min = 5, message = "A senha deve ter pelo menos 5 caracteres")
    private String password;

}