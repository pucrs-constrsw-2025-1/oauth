package com.constrsw.oauth.controller;

import com.constrsw.oauth.model.PasswordUpdateRequest;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakUserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class UserController {

    private final KeycloakUserService keycloakService;

    public UserController(KeycloakUserService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint is working");
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest) {
        System.out.println("chegou aqui " + userRequest);
        String createdUser = keycloakService.createUser(userRequest, false);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        keycloakService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/users?enabled=[true|false]
     * @param enabled filtro opcional
     * @return lista de usuários
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> listUsers(
            @RequestParam(required = false) Boolean enabled) {

        List<UserResponse> dtos = keycloakService.listUsers(enabled).stream()
            .map(keycloakService::toDto)
            .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/users/{id}
     * @param id ID do usuário
     * @return usuário encontrado
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        UserResponse dto = keycloakService.toDto(keycloakService.getUserById(id));
        return ResponseEntity.ok(dto);
    }

    /**
     * PUT /api/users/{id}
     * @param id   ID do usuário
     * @param rq   novos dados
     * @return 200 OK
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable String id,
            @RequestBody @Valid UserRequest rq) {

            keycloakService.updateUser(id, rq);
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH /api/users/{id}/password
     * @param id  ID do usuário
     * @param pw  novo password
     * @return 200 OK
     */
    @PatchMapping("/users/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String id,
            @RequestBody @Valid PasswordUpdateRequest pw) {

            keycloakService.updatePassword(id, pw.getPassword());
        return ResponseEntity.ok().build();
    }
}