package com.constrsw.oauth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.constrsw.oauth.dto.PasswordUpdateDTO;
import com.constrsw.oauth.dto.UserDTO;
import com.constrsw.oauth.service.KeycloakUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/users") 
public class UserController {

    @Autowired
    private KeycloakUserService userService;

    // Exemplo de como aceder ao token JWT ou ao Principal, se necessário no controlador
    // Normalmente, passaria apenas os dados de negócio para o serviço.
    @PostMapping
    @Operation(summary = "teste", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto,
                                              @AuthenticationPrincipal Jwt jwt) { // ou Authentication authentication
        // Se o userService precisar do token, pode passá-lo:
        // String accessToken = jwt.getTokenValue();
        // UserDTO createdUser = userService.createUser(accessToken, userDto);
        UserDTO createdUser = userService.createUser(userDto); // Idealmente, o serviço obtém o token se precisar
        return ResponseEntity.status(201).body(createdUser);
    }

    @GetMapping
    @Operation(summary = "Exemplo", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Exemplo", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        UserDTO user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Exemplo", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody UserDTO userDto) {
        userService.updateUser(id, userDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Exemplo", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> updatePassword(@PathVariable String id, @RequestBody PasswordUpdateDTO passwordDto) {
        userService.updatePassword(id, passwordDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exemplo", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> disableUser(@PathVariable String id) {
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Exemplo protegido", security = @SecurityRequirement(name = "bearerAuth"))
@GetMapping("/api/protegido")
public ResponseEntity<String> exemplo() {
    return ResponseEntity.ok("Protegido");
}   
}