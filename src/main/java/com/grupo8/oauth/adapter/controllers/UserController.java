package com.grupo8.oauth.adapter.controllers;

import com.grupo8.oauth.application.DTOs.UserDTO;
import com.grupo8.oauth.application.DTOs.UserRequestDTO;
import com.grupo8.oauth.application.service.ChangePassword;
import com.grupo8.oauth.application.service.CreateUser;
import com.grupo8.oauth.application.service.DeleteUser;
import com.grupo8.oauth.application.service.GetUser;
import com.grupo8.oauth.application.service.GetUsers;
import com.grupo8.oauth.application.service.UpdateUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUser createUser;
    private final GetUser getUser;
    private final GetUsers getUsers;
    private final UpdateUser updateUser;
    private final ChangePassword changePassword;
    private final DeleteUser deleteUser;

    @PostMapping
    @Operation(summary = "Criação de um usuário")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO userRequestDTO,
            @AuthenticationPrincipal Jwt jwt) {
        UserDTO userDTO = createUser.run(jwt.getTokenValue(), userRequestDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Recuperação de um usuário")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        UserDTO userDTO = getUser.run(jwt.getTokenValue(), id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    @Operation(summary = "Recuperação dos dados de todos os usuários cadastrados")
    public ResponseEntity<Collection<UserDTO>> getUsers(
            @RequestParam(required = false) @Schema(description = "Filtra usuários de acordo com seu estado - habilitado ou desabilitado") Boolean enabled,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(getUsers.run(jwt.getTokenValue(), enabled));
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Atualização de um usuário")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserRequestDTO userRequestDTO,
            @AuthenticationPrincipal Jwt jwt) {
        UserDTO userDTO = updateUser.run(jwt.getTokenValue(), id, userRequestDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping(path = "/{id}")
    @Operation(summary = "Atualização da senha de um usuário")
    public ResponseEntity<Void> changeUserPassword(@PathVariable UUID id, @RequestBody String newPassword,
            @AuthenticationPrincipal Jwt jwt) {
        changePassword.run(jwt.getTokenValue(), id, newPassword);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Exclusão lógica de um usuário")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        deleteUser.run(jwt.getTokenValue(), id);
        return ResponseEntity.noContent().build();
    }
}
