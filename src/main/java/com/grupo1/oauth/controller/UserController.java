package com.grupo1.oauth.controller;

import com.grupo1.oauth.dto.*;
import com.grupo1.oauth.service.KeycloakService;
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

import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final KeycloakService keycloakService;

    public UserController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @Operation(
            summary = "User authentication in Keycloak",
            description = "Receives username and password and returns an access_token if the credentials are valid."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Token generated successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "400",
              "error_description": "Invalid request.",
              "error_source": standardSource,
              "error_stack": [
                {
                  "exception": "org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest",
                  "message": "400 Bad Request",
                  "cause": "Required field not filled"
                }
              ]
            }
            """))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "401",
              "error_description": "Invalid credentials.",
              "error_source": standardSource,
              "error_stack": []
            }
            """))),
            @ApiResponse(responseCode = "403", description = "Unauthorized token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "403",
              "error_description": "Token does not have permission.",
              "error_source": standardSource,
              "error_stack": []
            }
            """))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "error_code": "500",
              "error_description": "Unexpected error.",
              "error_source": standardSource,
              "error_stack": [
                {
                  "exception": "java.lang.NullPointerException",
                  "message": "Could not access object",
                  "cause": "Null object in authentication"
                }
              ]
            }
            """)))
    })
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequest loginRequest) {
        TokenResponse token = keycloakService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @Operation(
            summary = "List Keycloak users",
            description = "Returns a list of users registered in Keycloak. It is possible to filter users by `enabled` status (active/inactive)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Token generated successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Invalid request.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest",
              "message": "400 Bad Request",
              "cause": "Required field not filled"
            }
          ]
        }
        """))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid credentials.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
                    @ApiResponse(responseCode = "403", description = "Unauthorized token",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "Token does not have permission.",
          "error_source": standardSource,
          "error_stack": []
        }
        """))),
                    @ApiResponse(responseCode = "500", description = "Unexpected error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Unexpected error.",
          "error_source": standardSource,
          "error_stack": [
            {
              "exception": "java.lang.NullPointerException",
              "message": "Could not access object",
              "cause": "Null object in authentication"
            }
          ]
        }
        """)))
    })
    @PostMapping("/users")
    public ResponseEntity<?> createUser( @RequestBody @Valid UserRequest user, @RequestHeader("Authorization") String token) {
        keycloakService.createUser(user, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User created successfully"));
    }

    @Operation(
            summary = "List Keycloak users",
            description = "Returns a list of users registered in Keycloak. It is possible to filter users by `enabled` status (active/inactive)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),

            @ApiResponse(responseCode = "401", description = "Invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid token.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "No permission to list users",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "No permission to list users.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while listing users",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Error while listing users.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Connection error",
              "cause": "Timeout in HTTP request"
            }
          ]
        }
        """)))
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(@RequestHeader("Authorization") String token, @RequestParam Optional<Boolean> enabled) {
        List<UserResponse> users = keycloakService.getUsers(token, enabled);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns the data of a specific user registered in Keycloak, given a valid authentication token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),

            @ApiResponse(responseCode = "401", description = "Invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid token.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "No permission to view user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "No permission to view user.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "User not found.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while fetching user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Error while fetching user.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal error",
              "cause": "Failed to communicate with Keycloak"
            }
          ]
        }
        """)))
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String token, @PathVariable String id) {
        UserResponse user = keycloakService.getUserById(token, id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Update user in Keycloak",
            description = "Updates data of an existing Keycloak user. Requires a valid token and the user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "User updated successfully"
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid token.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "No permission to update user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "No permission to update user.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "User not found for update",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "User not found for update.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while updating user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Error while updating user.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal error",
              "cause": "Failed to access Keycloak"
            }
          ]
        }
        """)))
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @PathVariable String id, @RequestBody @Valid UserRequest user) {
        keycloakService.updateUser(token, id, user);
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }

    @Operation(
            summary = "Change user password",
            description = "Changes the password of an existing user in Keycloak. Requires authentication token and the new password in the request body."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "Password updated successfully"
        }
        """))),

            @ApiResponse(responseCode = "400", description = "Missing or invalid 'password' field",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "400",
          "error_description": "Field 'password' is required.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid token.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "No permission to change password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "No permission to change password.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "User not found for password change",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "User not found for password change.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while changing password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Error while changing password.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal error",
              "cause": "Failed to access Keycloak"
            }
          ]
        }
        """)))
    })
    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token, @PathVariable String id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Field 'password' is required.");
        }
        keycloakService.updatePassword(token, id, password);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @Operation(
            summary = "Delete user",
            description = "Removes or deactivates a user in Keycloak. Requires authentication token and the user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "message": "User deleted successfully"
        }
        """))),

            @ApiResponse(responseCode = "401", description = "Invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "401",
          "error_description": "Invalid token.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "403", description = "No permission to delete user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "403",
          "error_description": "No permission to delete user.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "404", description = "User not found for deletion",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "404",
          "error_description": "User not found for deletion.",
          "error_source": "OAuthAPI",
          "error_stack": []
        }
        """))),

            @ApiResponse(responseCode = "500", description = "Unexpected error while deleting user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
        {
          "error_code": "500",
          "error_description": "Error while deleting user.",
          "error_source": "OAuthAPI",
          "error_stack": [
            {
              "exception": "java.lang.RuntimeException",
              "message": "Internal error",
              "cause": "Failed to communicate with Keycloak"
            }
          ]
        }
        """)))
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable String id) {
        keycloakService.disableUser(token, id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}
