package com.constrsw.oauth.interfaces.rest;

import com.constrsw.oauth.application.dto.role.RoleRequest;
import com.constrsw.oauth.application.dto.role.RoleResponse;
import com.constrsw.oauth.application.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Role Management API")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {
    
    private final RoleService roleService;
    
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @PostMapping
    @Operation(
            summary = "Create a new role",
            description = "Creates a new role with the provided information",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Role created successfully",
                            content = @Content(schema = @Schema(implementation = RoleResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "409", description = "Conflict - Role already exists")
            }
    )
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        RoleResponse createdRole = roleService.createRole(roleRequest);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(
            summary = "Get all roles",
            description = "Retrieves all roles",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Roles retrieved successfully",
                            content = @Content(schema = @Schema(implementation = RoleResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
            }
    )
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Get role by ID",
            description = "Retrieves a specific role by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Role retrieved successfully",
                            content = @Content(schema = @Schema(implementation = RoleResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Role not found")
            }
    )
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable String id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Update role",
            description = "Updates a role's information by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Role not found")
            }
    )
    public ResponseEntity<Void> updateRole(
            @PathVariable String id,
            @Valid @RequestBody RoleRequest roleRequest
    ) {
        roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update role",
            description = "Partially updates a role's information by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Role not found")
            }
    )
    public ResponseEntity<Void> patchRole(
            @PathVariable String id,
            @RequestBody RoleRequest roleRequest
    ) {
        roleService.patchRole(id, roleRequest);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete role",
            description = "Deletes a role by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Role not found")
            }
    )
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}