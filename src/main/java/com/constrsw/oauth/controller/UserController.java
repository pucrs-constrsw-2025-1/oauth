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
}