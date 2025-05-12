package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.RoleDTO;
import com.constrsw.oauth.service.KeycloakRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private KeycloakRoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDto) {
        RoleDTO created = roleService.createRole(roleDto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    @Operation(summary = "Get all roles", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleName}")
    @Operation(summary = "Get role by name", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RoleDTO> getRole(@PathVariable String roleName) {
        return ResponseEntity.ok(roleService.getRole(roleName));
    }

    @PutMapping("/{roleName}")
    @Operation(summary = "Update a role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> updateRole(@PathVariable String roleName, @RequestBody RoleDTO roleDto) {
        roleService.updateRole(roleName, roleDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{roleName}")
    @Operation(summary = "Partially update a role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> patchRole(@PathVariable String roleName, @RequestBody RoleDTO roleDto) {
        roleService.patchRole(roleName, roleDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleName}")
    @Operation(summary = "Logically delete (disable) a role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteRole(@PathVariable String roleName) {
        roleService.disableRole(roleName);
        return ResponseEntity.noContent().build();
    }
}
