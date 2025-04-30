package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.RoleRequest;
import com.constrsw.oauth.dto.RoleResponse;
import com.constrsw.oauth.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller para gerenciamento de roles
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Roles", description = "Endpoints para gerenciamento de roles")
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Criar uma nova role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role criada com sucesso"),
        @ApiResponse(responseCode = "409", description = "Role já existe")
    })
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        log.info("Criando role com nome: {}", roleRequest.getName());
        RoleResponse role = roleService.createRole(roleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @Operation(summary = "Listar todas as roles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    })
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("Listando todas as roles");
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(summary = "Obter role por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable String id) {
        log.info("Buscando role com ID: {}", id);
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @Operation(summary = "Atualizar role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Role não encontrada"),
        @ApiResponse(responseCode = "409", description = "Nome de role já existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRole(
            @PathVariable String id, 
            @Valid @RequestBody RoleRequest roleRequest) {
        log.info("Atualizando role com ID: {}", id);
        roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualizar parcialmente uma role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchRole(
            @PathVariable String id,
            @RequestBody RoleRequest roleRequest) {
        log.info("Atualizando parcialmente role com ID: {}", id);
        roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Excluir role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Role não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        log.info("Excluindo role com ID: {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}