package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.UserRequest;
import com.constrsw.oauth.dto.UserResponse;
import com.constrsw.oauth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller para gerenciamento de usuários
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Endpoints para gerenciamento de usuários")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "Criar um novo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "409", description = "Usuário já existe")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Criando usuário com username: {}", userRequest.getUsername());
        UserResponse user = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Listar todos os usuários")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @Parameter(description = "Filtrar por status (ativo/inativo)")
            @RequestParam(required = false) Boolean enabled) {
        log.info("Listando todos os usuários. Filtro enabled: {}", enabled);
        return ResponseEntity.ok(userService.getAllUsers(enabled));
    }

    @Operation(summary = "Obter usuário por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("Buscando usuário com ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Atualizar usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable String id, 
            @Valid @RequestBody UserRequest userRequest) {
        log.info("Atualizando usuário com ID: {}", id);
        userService.updateUser(id, userRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualizar senha de usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String id,
            @RequestBody String newPassword) {
        log.info("Atualizando senha do usuário com ID: {}", id);
        userService.updatePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Desativar usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableUser(@PathVariable String id) {
        log.info("Desativando usuário com ID: {}", id);
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }
}