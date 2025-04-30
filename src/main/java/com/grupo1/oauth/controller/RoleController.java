package com.grupo1.oauth.controller;

import com.grupo1.oauth.dto.ErrorResponse;
import com.grupo1.oauth.dto.RoleRequest;
import com.grupo1.oauth.dto.RoleResponse;
import com.grupo1.oauth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Create a new role",
            description = "Creates a new role in Keycloak based on the provided data. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Role created successfully"
        }
        """))),

            @ApiResponse(responseCode = "400", description = "Malformed request (validation error)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Invalid request.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
              "message": "Validation failed",
              "cause": "Field 'name' must not be blank"
            }
          ]
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while creating role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })    @PostMapping
    public ResponseEntity<?> createRole(@RequestHeader("Authorization") String token,
                                        @RequestBody @Valid RoleRequest roleRequest) {
        roleService.createRole(token, roleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Role created successfully"));
    }

    @Operation(
            summary = "Get all roles",
            description = "Retrieves a list of all roles registered in Keycloak. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while retrieving roles",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles(@RequestHeader("Authorization") String token) {
        List<RoleResponse> roles = roleService.getAllRoles(token);
        return ResponseEntity.ok(roles);
    }

    @Operation(
            summary = "Get role by ID",
            description = "Retrieves a specific role by its ID from Keycloak. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Role not found.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while retrieving role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@RequestHeader("Authorization") String token,
                                                    @PathVariable String id) {
        RoleResponse role = roleService.getRoleById(token, id);
        return ResponseEntity.ok(role);
    }

    @Operation(
            summary = "Update a role completely",
            description = "Updates all attributes of a role in Keycloak given its ID. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Role updated successfully"
        }
        """))),

            @ApiResponse(responseCode = "400", description = "Malformed request (validation error)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Invalid request.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
              "message": "Validation failed",
              "cause": "Field 'name' must not be blank"
            }
          ]
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "Role not found for update",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Role not found.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while updating role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@RequestHeader("Authorization") String token,
                                        @PathVariable String id,
                                        @RequestBody @Valid RoleRequest roleRequest) {
        roleService.updateRole(token, id, roleRequest);
        return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
    }

    @Operation(
            summary = "Update a role partially",
            description = "Performs a partial update on a role in Keycloak by its ID. Only the fields provided in the request body will be updated. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role partially updated successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Role partially updated successfully"
        }
        """))),

            @ApiResponse(responseCode = "400", description = "Malformed or invalid update data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Invalid request.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest",
              "message": "400 Bad Request",
              "cause": "Invalid update payload"
            }
          ]
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "Role not found for update",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Role not found.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while updating role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchRole(@RequestHeader("Authorization") String token,
                                       @PathVariable String id,
                                       @RequestBody Map<String, Object> updates) {
        roleService.patchRole(token, id, updates);
        return ResponseEntity.ok(Map.of("message", "Role partially updated successfully"));
    }

    @Operation(
            summary = "Logically delete a role",
            description = "Performs a logical deletion of a role in Keycloak by its ID. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Role deleted successfully"
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "Role not found for deletion",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Role not found.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while deleting role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@RequestHeader("Authorization") String token,
                                        @PathVariable String id) {
        roleService.deleteRole(token, id);
        return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
    }

    @Operation(
            summary = "Assign a role to a user",
            description = "Assigns an existing role in Keycloak to a user by their respective IDs. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned to user successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Role assigned to user successfully"
        }
        """))),

            @ApiResponse(responseCode = "400", description = "Invalid role or user ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Invalid request.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.IllegalArgumentException",
              "message": "Invalid role ID",
              "cause": "Malformed input"
            }
          ]
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "Role or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Role or user not found.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while assigning role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })
    @PostMapping("/{roleId}/assign/{userId}")
    public ResponseEntity<?> assignRoleToUser(@RequestHeader("Authorization") String token,
                                              @PathVariable String roleId,
                                              @PathVariable String userId) {
        roleService.assignRoleToUser(token, roleId, userId);
        return ResponseEntity.ok(Map.of("message", "Role assigned to user successfully"));
    }

    @Operation(
            summary = "Remove a role from a user",
            description = "Removes an assigned role from a user in Keycloak by their respective IDs. Requires a valid bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role removed from user successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Role removed from user successfully"
        }
        """))),

            @ApiResponse(responseCode = "400", description = "Invalid role or user ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Invalid request.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.IllegalArgumentException",
              "message": "Invalid user or role ID",
              "cause": "Malformed input"
            }
          ]
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "Token does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "Role or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "Role or user not found.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while removing role",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal server error",
              "cause": "Keycloak communication failure"
            }
          ]
        }
        """)))
    })
    @PostMapping("/{roleId}/remove/{userId}")
    public ResponseEntity<?> removeRoleFromUser(@RequestHeader("Authorization") String token,
                                                @PathVariable String roleId,
                                                @PathVariable String userId) {
        roleService.removeRoleFromUser(token, roleId, userId);
        return ResponseEntity.ok(Map.of("message", "Role removed from user successfully"));
    }
}
