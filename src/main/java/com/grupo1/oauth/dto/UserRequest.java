package com.grupo1.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class UserRequest {

    @Schema(example = "usuario@email.com")
    @NotBlank(message = "Username (e-mail) é obrigatório")
    @Pattern(
            regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "E-mail inválido conforme RFC"
    )
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @JsonProperty("first-name")
    private String firstName;

    @JsonProperty("last-name")
    private String lastName;
}
