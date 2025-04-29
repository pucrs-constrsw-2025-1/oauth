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
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.constrsw.oauth.dto.RoleAssignmentRequest;
import com.constrsw.oauth.dto.RoleResponse;
import com.constrsw.oauth.exception.ErrorResponse;
import com.constrsw.oauth.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Roles", description = "Endpoints para gerenciamento de associação entre usuários e roles")
public class UserRoleController {

    private final RoleService roleService;

    @Operation(
        summary = "Listar roles de um usuário",
        description = "Retorna a lista de todas as roles atribuídas a um usuário específico"
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
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RoleResponse>> getUserRoles(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String userId) {
        return ResponseEntity.ok(roleService.getUserRoles(userId));
    }

    @Operation(
        summary = "Atribuir roles a um usuário",
        description = "Atribui uma ou mais roles a um usuário específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Roles atribuídas com sucesso"
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
            description = "Usuário ou role não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignRolesToUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String userId,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "IDs das roles a serem atribuídas ao usuário",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = RoleAssignmentRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Exemplo de requisição",
                            value = "{\n" +
                                    "  \"roleIds\": [\n" +
                                    "    \"f47ac10b-58cc-4372-a567-0e02b2c3d479\",\n" +
                                    "    \"d93e8582-47b8-4e30-9c5f-aa34d6b9d46c\"\n" +
                                    "  ]\n" +
                                    "}"
                        )
                    }
                )
            )
            @RequestBody RoleAssignmentRequest request) {
        roleService.assignRolesToUser(userId, request.getRoleIds());
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Remover roles de um usuário",
        description = "Remove uma ou mais roles de um usuário específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Roles removidas com sucesso"
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
            description = "Usuário ou role não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeRolesFromUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String userId,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "IDs das roles a serem removidas do usuário",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = RoleAssignmentRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Exemplo de requisição",
                            value = "{\n" +
                                    "  \"roleIds\": [\n" +
                                    "    \"f47ac10b-58cc-4372-a567-0e02b2c3d479\",\n" +
                                    "    \"d93e8582-47b8-4e30-9c5f-aa34d6b9d46c\"\n" +
                                    "  ]\n" +
                                    "}"
                        )
                    }
                )
            )
            @RequestBody RoleAssignmentRequest request) {
        roleService.removeRolesFromUser(userId, request.getRoleIds());
        return ResponseEntity.ok().build();
    }
}