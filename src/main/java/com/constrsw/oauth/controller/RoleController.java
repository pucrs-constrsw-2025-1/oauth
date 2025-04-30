package com.constrsw.oauth.controller;

import com.constrsw.oauth.model.RoleRequest;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.usecases.interfaces.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class RoleController {

    private final ICreateRoleUseCase createRoleUseCase;
    private final IGetAllRolesUseCase getAllRolesUseCase;
    private final IGetRoleByIdUseCase getRoleByIdUseCase;
    private final IUpdateRoleUseCase updateRoleUseCase;
    private final IPatchRoleUseCase patchRoleUseCase;
    private final IDeleteRoleUseCase deleteRoleUseCase;
    private final IAssignRoleToUserUseCase assignRoleToUserUseCase;
    private final IRemoveRoleFromUserUseCase removeRoleFromUserUseCase;

    @PostMapping("/roles")
    public ResponseEntity<Void> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        createRoleUseCase.execute(roleRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = getAllRolesUseCase.execute();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable String id) {
        RoleResponse role = getRoleByIdUseCase.execute(id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<Void> updateRole(@PathVariable String id, @Valid @RequestBody RoleRequest roleRequest) {
        updateRoleUseCase.execute(id, roleRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/roles/{id}")
    public ResponseEntity<Void> patchRole(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        patchRoleUseCase.execute(id, updates);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        deleteRoleUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable String userId, @PathVariable String roleId) {
        assignRoleToUserUseCase.execute(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable String userId, @PathVariable String roleId) {
        removeRoleFromUserUseCase.execute(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}