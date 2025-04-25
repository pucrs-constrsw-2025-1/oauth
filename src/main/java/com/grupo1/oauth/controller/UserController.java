package com.grupo1.oauth.controller;

import com.grupo1.oauth.dto.*;
import com.grupo1.oauth.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final KeycloakService keycloakService;

    public UserController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @Operation(
            summary = "Autenticação de usuário no Keycloak",
            description = "Recebe username e password e retorna um access_token se as credenciais forem válidas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Token gerado com sucesso",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição malformada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "400",
              "error_description": "Requisição inválida.",
              "error_source": standardSource,
              "error_stack": [
                {
                  "exception": "org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest",
                  "message": "400 Bad Request",
                  "cause": "Campo obrigatório não preenchido"
                }
              ]
            }
            """))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "401",
              "error_description": "Credenciais inválidas.",
              "error_source": standardSource,
              "error_stack": []
            }
            """))),
            @ApiResponse(responseCode = "403", description = "Token sem permissão",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "403",
              "error_description": "Token sem permissão.",
              "error_source": standardSource,
              "error_stack": []
            }
            """))),
            @ApiResponse(responseCode = "500", description = "Erro inesperado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "500",
              "error_description": "Erro inesperado.",
              "error_source": standardSource,
              "error_stack": [
                {
                  "exception": "java.lang.NullPointerException",
                  "message": "Não foi possível acessar o objeto",
                  "cause": "Objeto nulo na autenticação"
                }
              ]
            }
            """)))
    })
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequest loginRequest) {
        TokenResponse token = keycloakService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @Operation(
            summary = "Criação de usuário no Keycloak",
            description = "Cria um novo usuário no Keycloak com os dados fornecidos, desde que o token de autorização seja válido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário criado com sucesso\"}"))),
            @ApiResponse(responseCode = "400", description = "Estrutura inválida ou e-mail inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Estrutura inválida ou e-mail inválido.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest",
              "message": "400 Bad Request",
              "cause": "E-mail em formato inválido"
            }
          ]
        }
        """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Token inválido.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para criar usuário",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Sem permissão para criar usuário.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "409", description = "Usuário já existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "409",
          "error_description": "Usuário já existe.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro inesperado no servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Erro na criação do usuário.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "java.lang.NullPointerException",
              "message": "Erro inesperado",
              "cause": "Objeto nulo ao processar usuário"
            }
          ]
        }
        """)))
    })
    @PostMapping("/users")
    public ResponseEntity<?> createUser( @RequestBody @Valid UserRequest user, @RequestHeader("Authorization") String token) {
        keycloakService.createUser(user, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuário criado com sucesso"));
    }

    @Operation(
            summary = "Listagem de usuários do Keycloak",
            description = "Retorna a lista de usuários cadastrados no Keycloak. É possível filtrar usuários pelo status `enabled` (ativos/inativos)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Token inválido.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para listar usuários",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Sem permissão para listar usuários.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao listar usuários",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Erro na listagem de usuários.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Erro de conexão",
              "cause": "Timeout na chamada HTTP"
            }
          ]
        }
        """)))
    })
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String token, @RequestParam Optional<Boolean> enabled) {
        List<UserResponse> users = keycloakService.getUsers(token);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário específico cadastrado no Keycloak, desde que o token de autenticação seja válido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Token inválido.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para visualizar usuário",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Sem permissão para visualizar usuário.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Usuário não encontrado.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao buscar usuário",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Erro ao buscar usuário.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Erro interno",
              "cause": "Falha na comunicação com Keycloak"
            }
          ]
        }
        """)))
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String token, @PathVariable String id) {
        UserResponse user = keycloakService.getUserById(token, id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Atualização de usuário no Keycloak",
            description = "Atualiza os dados de um usuário existente no Keycloak. É necessário fornecer um token válido e o ID do usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário atualizado com sucesso\"}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Token inválido.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar usuário",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Sem permissão para atualizar usuário.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Usuário não encontrado para atualização.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao atualizar usuário",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Erro ao atualizar usuário.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Erro interno",
              "cause": "Falha ao acessar Keycloak"
            }
          ]
        }
        """)))
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @PathVariable String id, @RequestBody @Valid UserRequest user) {
        keycloakService.updateUser(token, id, user);
        return ResponseEntity.ok(Map.of("message", "Usuário atualizado com sucesso"));
    }

    @Operation(
            summary = "Alteração de senha de usuário",
            description = "Altera a senha de um usuário existente no Keycloak. É necessário fornecer o token de autenticação e o novo valor da senha no corpo da requisição."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Senha atualizada com sucesso\"}"))),
            @ApiResponse(responseCode = "400", description = "Campo 'password' ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Campo 'password' é obrigatório.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Token inválido.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para alterar senha",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Sem permissão para alterar senha.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Usuário não encontrado para alterar senha.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao alterar senha",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Erro ao alterar senha.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Erro interno",
              "cause": "Falha ao acessar Keycloak"
            }
          ]
        }
        """)))
    })
    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token, @PathVariable String id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Campo 'password' é obrigatório.");
        }
        keycloakService.updatePassword(token, id, password);
        return ResponseEntity.ok(Map.of("message", "Senha atualizada com sucesso"));
    }

    @Operation(
            summary = "Remoção de usuário",
            description = "Remove ou desativa um usuário do Keycloak. É necessário fornecer um token de autenticação e o ID do usuário a ser removido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário removido com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário removido com sucesso\"}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Token inválido.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para deletar usuário",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Sem permissão para deletar usuário.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Usuário não encontrado para remoção.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao deletar usuário",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Erro ao deletar usuário.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Erro interno",
              "cause": "Falha na comunicação com Keycloak"
            }
          ]
        }
        """)))
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable String id) {
        keycloakService.disableUser(token, id);
        return ResponseEntity.ok(Map.of("message", "Usuário removido com sucesso"));
    }
}
