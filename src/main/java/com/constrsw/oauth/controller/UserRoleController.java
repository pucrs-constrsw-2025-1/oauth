package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.RoleResponse;
import com.constrsw.oauth.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller para gerenciamento da associação entre usuários e roles
 */
@RestController
@RequestMapping("/users/{userId}/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Roles", description = "Endpoints para gerenciamento de associação entre usuários e roles")
@Slf4j
public class UserRoleController {

    private final RoleService roleService;

    @Operation(summary = "Listar roles de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getUserRoles(@PathVariable String userId) {
        log.info("Listando roles do usuário com ID: {}", userId);
        return ResponseEntity.ok(roleService.getUserRoles(userId));
    }

    @Operation(summary = "Atribuir roles a um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles atribuídas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrada")
    })
    @PostMapping
    public ResponseEntity<Void> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        log.info("Atribuindo roles ao usuário com ID: {}", userId);
        roleService.assignRolesToUser(userId, roleIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover roles de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles removidas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrada")
    })
    @DeleteMapping
    public ResponseEntity<Void> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        log.info("Removendo roles do usuário com ID: {}", userId);
        roleService.removeRolesFromUser(userId, roleIds);
        return ResponseEntity.ok().build();
    }
}