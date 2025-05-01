package com.grupo_4.oauth.controller;

import com.grupo_4.oauth.model.RoleResponse;
import com.grupo_4.oauth.model.RoleRequest;
import com.grupo_4.oauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        List<RoleResponse> roles = roleService.getAllRoles(accessToken);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable("roleId") String roleId, @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        RoleResponse role = roleService.getRoleById(accessToken, roleId);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<Void> createRole(@RequestHeader("Authorization") String authorizationHeader, @RequestBody RoleRequest request) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        roleService.createRole(accessToken, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Void> updateRole(@PathVariable("roleId") String roleId, @RequestHeader("Authorization") String authorizationHeader, @RequestBody RoleRequest request) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        roleService.updateRole(accessToken, roleId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{roleId}")
    public ResponseEntity<Void> patchRole(@PathVariable("roleId") String roleId, @RequestHeader("Authorization") String authorizationHeader, @RequestBody Map<String, Object> updates) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        roleService.patchRole(accessToken, roleId, updates);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleId") String roleId, @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        roleService.deleteRole(accessToken, roleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign/{userId}/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable("userId") String userId, @PathVariable("roleId") String roleId, @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        roleService.assignRoleToUser(accessToken, userId, roleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/assign/{userId}/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable("userId") String userId, @PathVariable("roleId") String roleId, @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        roleService.removeRoleFromUser(accessToken, userId, roleId);
        return ResponseEntity.noContent().build();
    }
} 