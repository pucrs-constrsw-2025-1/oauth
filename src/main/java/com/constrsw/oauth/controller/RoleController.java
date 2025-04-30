package com.constrsw.oauth.controller;

import com.constrsw.oauth.model.RoleRequest;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.usecases.interfaces.*;
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

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Perfis", description = "API para gerenciamento de perfis de acesso")
public class RoleController {

    private final ICreateRoleUseCase createRoleUseCase;
    private final IGetAllRolesUseCase getAllRolesUseCase;
    private final IGetRoleByIdUseCase getRoleByIdUseCase;
    private final IUpdateRoleUseCase updateRoleUseCase;
    private final IPatchRoleUseCase patchRoleUseCase;
    private final IDeleteRoleUseCase deleteRoleUseCase;
    private final IAssignRoleToUserUseCase assignRoleToUserUseCase;
    private final IRemoveRoleFromUserUseCase removeRoleFromUserUseCase;

    @PostMapping("/roles")
    @Operation(
            summary = "Criar perfil",
            description = "Cria um novo perfil de acesso no sistema com base nos dados fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Perfil já existe")
    })
    public ResponseEntity<Void> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        createRoleUseCase.execute(roleRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    @Operation(
            summary = "Listar perfis",
            description = "Retorna a lista de todos os perfis de acesso cadastrados no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de perfis recuperada com sucesso",
                    content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = getAllRolesUseCase.execute();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/{id}")
    @Operation(
            summary = "Buscar perfil por ID",
            description = "Retorna os dados de um perfil específico com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<RoleResponse> getRoleById(
            @Parameter(description = "ID do perfil")
            @PathVariable String id) {
        RoleResponse role = getRoleByIdUseCase.execute(id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/roles/{id}")
    @Operation(
            summary = "Atualizar perfil",
            description = "Atualiza todos os dados de um perfil existente com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "ID do perfil")
            @PathVariable String id,
            @Valid @RequestBody RoleRequest roleRequest) {
        updateRoleUseCase.execute(id, roleRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/roles/{id}")
    @Operation(
            summary = "Atualizar perfil parcialmente",
            description = "Atualiza dados específicos de um perfil existente com base no ID fornecido, "
                    + "mantendo os campos não informados inalterados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil atualizado parcialmente com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<Void> patchRole(
            @Parameter(description = "ID do perfil")
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        patchRoleUseCase.execute(id, updates);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/roles/{id}")
    @Operation(
            summary = "Excluir perfil",
            description = "Remove um perfil do sistema com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado"),
            @ApiResponse(responseCode = "409", description = "Perfil não pode ser excluído pois está sendo utilizado")
    })
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID do perfil")
            @PathVariable String id) {
        deleteRoleUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/roles/{roleId}")
    @Operation(
            summary = "Atribuir perfil a usuário",
            description = "Atribui um perfil específico a um usuário existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil atribuído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou perfil não encontrado")
    })
    public ResponseEntity<Void> assignRoleToUser(
            @Parameter(description = "ID do usuário")
            @PathVariable String userId,
            @Parameter(description = "ID do perfil")
            @PathVariable String roleId) {
        assignRoleToUserUseCase.execute(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    @Operation(
            summary = "Remover perfil de usuário",
            description = "Remove um perfil específico de um usuário existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil removido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou perfil não encontrado")
    })
    public ResponseEntity<Void> removeRoleFromUser(
            @Parameter(description = "ID do usuário")
            @PathVariable String userId,
            @Parameter(description = "ID do perfil")
            @PathVariable String roleId) {
        removeRoleFromUserUseCase.execute(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}