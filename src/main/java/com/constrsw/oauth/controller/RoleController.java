package com.constrsw.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.constrsw.oauth.dto.RoleRequest;
import com.constrsw.oauth.dto.RoleResponse;
import com.constrsw.oauth.exception.ErrorResponse;
import com.constrsw.oauth.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Roles", description = "Endpoints para gerenciamento de roles")
public class RoleController {

    private final RoleService roleService;

    @Operation(
        summary = "Criar uma nova role",
        description = "Cria uma nova role no sistema com os dados fornecidos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Role criada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoleResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Exemplo de resposta",
                        value = "{\n" +
                                "  \"id\": \"f47ac10b-58cc-4372-a567-0e02b2c3d479\",\n" +
                                "  \"name\": \"admin\",\n" +
                                "  \"description\": \"Administrador do sistema\",\n" +
                                "  \"composite\": false,\n" +
                                "  \"clientRole\": false,\n" +
                                "  \"containerId\": \"constrsw\"\n" +
                                "}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para acessar este recurso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Nome da role já existente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleResponse> createRole(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da role a ser criada",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = RoleRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Exemplo de requisição",
                            value = "{\n" +
                                    "  \"name\": \"admin\",\n" +
                                    "  \"description\": \"Administrador do sistema\"\n" +
                                    "}"
                        )
                    }
                )
            )
            @Valid @RequestBody RoleRequest roleRequest) {
        RoleResponse role = roleService.createRole(roleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @Operation(
        summary = "Listar todas as roles",
        description = "Retorna a lista de todas as roles cadastradas no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operação bem-sucedida",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = RoleResponse.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para acessar este recurso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(
        summary = "Obter role por ID",
        description = "Retorna os dados de uma role específica com base no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operação bem-sucedida",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para acessar este recurso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Role não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleResponse> getRoleById(
            @Parameter(description = "ID da role", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @Operation(
        summary = "Atualizar role",
        description = "Atualiza os dados de uma role específica com base no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role atualizada com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para acessar este recurso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Role não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Nome da role já existente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "ID da role", required = true)
            @PathVariable String id,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados da role",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = RoleRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Exemplo de requisição",
                            value = "{\n" +
                                    "  \"name\": \"admin\",\n" +
                                    "  \"description\": \"Administrador do sistema com acesso total\"\n" +
                                    "}"
                        )
                    }
                )
            )
            @Valid @RequestBody RoleRequest roleRequest) {
        roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Atualizar parcialmente uma role",
        description = "Atualiza parcialmente os dados de uma role específica com base no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role atualizada com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para acessar este recurso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Role não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> patchRole(
            @Parameter(description = "ID da role", required = true)
            @PathVariable String id,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados parciais da role a serem atualizados",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = RoleRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Exemplo de requisição",
                            value = "{\n" +
                                    "  \"description\": \"Nova descrição da role\"\n" +
                                    "}"
                        )
                    }
                )
            )
            @RequestBody RoleRequest roleRequest) {
        roleService.patchRole(id, roleRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Excluir role",
        description = "Exclui uma role específica com base no ID fornecido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Role excluída com sucesso"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para acessar este recurso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Role não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID da role", required = true)
            @PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}