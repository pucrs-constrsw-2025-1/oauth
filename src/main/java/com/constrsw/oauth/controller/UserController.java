package com.constrsw.oauth.controller;
import java.util.List;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.usecases.interfaces.*;
import com.constrsw.oauth.usecases.user.DeleteUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UserController {

    private final ICreateUserUseCase createUserUseCase;
    private final IGetAllUsersUseCase getAllUsersUseCase;
    private final IGetUserByIdUseCase getUserByIdUseCase;
    private final IUpdateUserUseCase updateUserUseCase;
    private final IUpdatePasswordUseCase updatePasswordUseCase;
    private final IDeleteUserUseCase deleteUserUseCase;

    @PostMapping("/users")
    @Operation(
            summary = "Criar usuário",
            description = "Cria um novo usuário no sistema com base nos dados fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Nome de usuário já existe")
    })
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequest userRequest) {
        String userId = createUserUseCase.execute(userRequest, false);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    @Operation(
            summary = "Lista usuários",
            description = "Retorna a lista de todos os usuários cadastrados no sistema. "
                    + "É possível filtrar por status de ativação com o parâmetro enabled."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários recuperada com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<UserResponse>> listUsers(
            @Parameter(description = "Filtro por status de ativação do usuário")
            @RequestParam(required = false) Boolean enabled) {
        List<UserResponse> users = getAllUsersUseCase.execute(enabled);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário específico com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "ID do usuário")
            @PathVariable String id) {
        UserResponse user = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza os dados de um usuário existente com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "ID do usuário")
            @PathVariable String id,
            @Valid @RequestBody UserRequest userRequest) {
        updateUserUseCase.execute(id, userRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/password")
    @Operation(
            summary = "Atualizar senha",
            description = "Atualiza a senha de um usuário existente com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha inválida"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "ID do usuário")
            @PathVariable String id,
            @RequestBody String newPassword) {
        updatePasswordUseCase.execute(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    @Operation(
            summary = "Excluir usuário",
            description = "Remove um usuário do sistema com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário")
            @PathVariable String id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}