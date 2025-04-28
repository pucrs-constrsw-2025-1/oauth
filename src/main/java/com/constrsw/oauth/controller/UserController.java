package com.constrsw.oauth.controller;

import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.model.UserResponse;

import com.constrsw.oauth.service.KeycloakUserService;
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

    private final KeycloakUserService keycloakService;

    @Autowired
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

    /** -----------------------------------------------------------
     *  GET /api/users
     *  Ex.: /api/users?enabled=true
     *  ----------------------------------------------------------- */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> listUsers(
            @RequestParam(required = false) Boolean enabled) {

        List<UserResponse> result = keycloakService
                .listUsers(enabled)              // serviço
                .stream()
                .map(this::toResponse)           // converte p/ DTO
                .toList();

        return ResponseEntity.ok(result);        // 200
    }

    /** -----------------------------------------------------------
     *  GET /api/users/{id}
     *  ----------------------------------------------------------- */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {

        UserResponse dto = toResponse(keycloakService.getUserById(id));
        return ResponseEntity.ok(dto);           // 200
    }

    /* --------- utilitário de conversão Domain → DTO --------- */
    private UserResponse toResponse(org.keycloak.representations.idm.UserRepresentation u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .enabled(u.isEnabled())
                .build();
    }
}