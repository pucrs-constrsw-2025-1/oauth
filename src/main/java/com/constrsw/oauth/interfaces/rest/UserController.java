package com.constrsw.oauth.interfaces.rest;

import com.constrsw.oauth.application.dto.user.PasswordRequest;
import com.constrsw.oauth.application.dto.user.UserRequest;
import com.constrsw.oauth.application.dto.user.UserResponse;
import com.constrsw.oauth.application.service.UserService;
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
@RequestMapping("/users")
@Tag(name = "Users", description = "User Management API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided information",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure or email"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "409", description = "Conflict - Username already exists")
            }
    )
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieves all users or filters by enabled status if provided",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Users retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
            }
    )
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) Boolean enabled
    ) {
        List<UserResponse> users;
        if (enabled != null) {
            users = userService.getUsersByEnabled(enabled);
        } else {
            users = userService.getAllUsers();
        }
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a specific user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - User not found")
            }
    )
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Update user",
            description = "Updates a user's information by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - User not found")
            }
    )
    public ResponseEntity<Void> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequest userRequest
    ) {
        userService.updateUser(id, userRequest);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}")
    @Operation(
            summary = "Update user password",
            description = "Updates a user's password by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - User not found")
            }
    )
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable String id,
            @Valid @RequestBody PasswordRequest passwordRequest
    ) {
        userService.updateUserPassword(id, passwordRequest.getPassword());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Disable user",
            description = "Logically deletes (disables) a user by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User disabled successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - User not found")
            }
    )
    public ResponseEntity<Void> disableUser(@PathVariable String id) {
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{userId}/roles/{roleId}")
    @Operation(
            summary = "Add role to user",
            description = "Assigns a role to a user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Role assigned successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - User or role not found")
            }
    )
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable String userId,
            @PathVariable String roleId
    ) {
        userService.addRoleToUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{userId}/roles/{roleId}")
    @Operation(
            summary = "Remove role from user",
            description = "Removes a role from a user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Role removed successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request structure"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid access token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Not Found - User or role not found")
            }
    )
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable String userId,
            @PathVariable String roleId
    ) {
        userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}