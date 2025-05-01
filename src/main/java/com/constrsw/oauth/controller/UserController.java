package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.UserRequest;
import com.constrsw.oauth.dto.UserResponse;
import com.constrsw.oauth.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações relacionadas a usuários
 */
@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Cria um novo usuário - aceita parâmetros via URL ou JSON
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @Valid @RequestBody(required = false) UserRequest userRequestBody) {
        
        UserRequest userRequest;
        
        // Verifica se os dados vieram via parâmetros da URL ou via corpo da requisição
        if (username != null && password != null && firstName != null && lastName != null) {
            // Dados vieram como parâmetros da URL
            userRequest = UserRequest.builder()
                    .username(username)
                    .password(password)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
        } else if (userRequestBody != null) {
            // Dados vieram no corpo da requisição
            userRequest = userRequestBody;
        } else {
            // Dados insuficientes
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Recebida requisição para criar usuário: {}", userRequest.getUsername());
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Recupera todos os usuários, com filtro opcional por status
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String username) {
        
        if (username != null && !username.isEmpty()) {
            log.info("Recebida requisição para buscar usuário por username: {}", username);
            List<UserResponse> users = userService.getUserByUsername(username);
            return ResponseEntity.ok(users);
        } else {
            log.info("Recebida requisição para listar usuários. Filtro enabled: {}", enabled);
            List<UserResponse> users = enabled != null ? 
                userService.getAllUsers(enabled) : 
                userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
    }

    /**
     * Recupera um usuário pelo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("Recebida requisição para buscar usuário por ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Atualiza os dados de um usuário
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequest userRequest) {
        log.info("Recebida requisição para atualizar usuário: {}", id);
        userService.updateUser(id, userRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza a senha de um usuário
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String id,
            @RequestBody PasswordRequest passwordRequest) {
        log.info("Recebida requisição para atualizar senha do usuário: {}", id);
        userService.updatePassword(id, passwordRequest.getPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     * Desativa um usuário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableUser(@PathVariable String id) {
        log.info("Recebida requisição para desativar usuário: {}", id);
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * DTO para requisição de alteração de senha
     */
    @lombok.Data
    private static class PasswordRequest {
        private String password;
    }
}