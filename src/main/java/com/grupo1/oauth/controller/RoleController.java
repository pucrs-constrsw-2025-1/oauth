package com.grupo1.oauth.controller;

import com.grupo1.oauth.dto.RoleRequest;
import com.grupo1.oauth.dto.RoleResponse;
import com.grupo1.oauth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Create a new role")
    @PostMapping
    public ResponseEntity<?> createRole(@RequestHeader("Authorization") String token,
                                        @RequestBody @Valid RoleRequest roleRequest) {
        roleService.createRole(token, roleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Role created successfully"));
    }

    @Operation(summary = "Get all roles")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles(@RequestHeader("Authorization") String token) {
        List<RoleResponse> roles = roleService.getAllRoles(token);
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@RequestHeader("Authorization") String token,
                                                    @PathVariable String id) {
        RoleResponse role = roleService.getRoleById(token, id);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Update a role completely")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@RequestHeader("Authorization") String token,
                                        @PathVariable String id,
                                        @RequestBody @Valid RoleRequest roleRequest) {
        roleService.updateRole(token, id, roleRequest);
        return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
    }

    @Operation(summary = "Update a role partially")
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchRole(@RequestHeader("Authorization") String token,
                                       @PathVariable String id,
                                       @RequestBody Map<String, Object> updates) {
        roleService.patchRole(token, id, updates);
        return ResponseEntity.ok(Map.of("message", "Role partially updated successfully"));
    }

    @Operation(summary = "Logically delete a role")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@RequestHeader("Authorization") String token,
                                        @PathVariable String id) {
        roleService.deleteRole(token, id);
        return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
    }

    @Operation(summary = "Assign a role to a user")
    @PostMapping("/{roleId}/assign/{userId}")
    public ResponseEntity<?> assignRoleToUser(@RequestHeader("Authorization") String token,
                                              @PathVariable String roleId,
                                              @PathVariable String userId) {
        roleService.assignRoleToUser(token, roleId, userId);
        return ResponseEntity.ok(Map.of("message", "Role assigned to user successfully"));
    }

    @Operation(summary = "Remove a role from a user")
    @PostMapping("/{roleId}/remove/{userId}")
    public ResponseEntity<?> removeRoleFromUser(@RequestHeader("Authorization") String token,
                                                @PathVariable String roleId,
                                                @PathVariable String userId) {
        roleService.removeRoleFromUser(token, roleId, userId);
        return ResponseEntity.ok(Map.of("message", "Role removed from user successfully"));
    }
}
