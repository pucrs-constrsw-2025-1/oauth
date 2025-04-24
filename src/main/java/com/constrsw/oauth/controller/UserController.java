package com.constrsw.oauth.controller;

import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final KeycloakService keycloakService;

    @Autowired
    public UserController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint is working");
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        System.out.println("chegou aqui " + userRequest);
        UserResponse createdUser = keycloakService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        AccessTokenResponse token = keycloakService.login(username, password);
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        keycloakService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = keycloakService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        UserResponse user = keycloakService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // Rota protegida que requer role ADMINISTRADOR
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/users/admin/list")
    public ResponseEntity<List<UserResponse>> getAdminUsersList() {
        List<UserResponse> users = keycloakService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}