package com.grupo1.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo1.oauth.configuration.MockKeycloakServiceConfig;
import com.grupo1.oauth.dto.LoginRequest;
import com.grupo1.oauth.dto.TokenResponse;
import com.grupo1.oauth.dto.UserRequest;
import com.grupo1.oauth.dto.UserResponse;
import com.grupo1.oauth.service.KeycloakService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(MockKeycloakServiceConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        Mockito.reset(keycloakService);
    }

    /**
     * ========================
     *     LOGIN TESTS
     * ========================
     */
    @Test
    @DisplayName("Should return 201 when login is successful")
    void loginSuccess() throws Exception {
        TokenResponse token = new TokenResponse();
        token.setAccessToken("fake-token");
        token.setExpiresIn(300L);
        token.setRefreshToken("refresh-token");
        token.setTokenType("bearer");

        Mockito.when(keycloakService.login(any(LoginRequest.class))).thenReturn(token);

        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("fake-token"));
    }

    @Test
    @DisplayName("Should return 400 when the body is malformed")
    void loginError400() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content("{\"username\":\"user\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 when using invalid credentials")
    void loginError401() throws Exception {
        Mockito.when(keycloakService.login(any(LoginRequest.class)))
                .thenThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        null, null, StandardCharsets.UTF_8));

        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 500 when an unexpected error occurs")
    void loginError500() throws Exception {
        Mockito.when(keycloakService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Internal Error"));

        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /*
     * ============================
     *     USER LISTING TESTS
     * ============================
     */

    /*
    @Test
    @DisplayName("getUsers - Should return 200 with user list if token is valid")
    void getUsersSuccess() throws Exception {
        UserResponse user1 = new UserResponse();
        user1.setId("1");
        user1.setUsername("user1@email.com");

        UserResponse user2 = new UserResponse();
        user2.setId("2");
        user2.setUsername("user2@email.com");

        List<UserResponse> users = List.of(user1, user2);

        Mockito.when(keycloakService.getUsers(anyString(), Optional.empty() )).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1@email.com"));
    }

    @Test
    @DisplayName("getUsers - Should return 401 if token is invalid during user listing")
    void getUsersInvalidToken() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUsers(anyString(), Optional.empty());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getUsers - Should return 403 if not allowed to list users")
    void getUsersForbidden() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUsers(anyString(), Optional.empty());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer no-permission-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("getUsers - Should return 500 when an unexpected error occurs during user listing")
    void getUsersInternalError() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(keycloakService).getUsers(anyString(), Optional.empty());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer any-token"))
                .andExpect(status().isInternalServerError());
    }
    */

    /**
     * ==============================
     *     USER CREATION TESTS
     * ==============================
     */
    @Test
    @DisplayName("createUser - Should return 201 when user is successfully created")
    void createUserSuccess() throws Exception {
        Mockito.doNothing().when(keycloakService).createUser(any(UserRequest.class), any(String.class));

        UserRequest user = new UserRequest();
        user.setUsername("novo@teste.com");
        user.setPassword("Password123!");
        user.setFirstName("New");
        user.setLastName("User");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    @DisplayName("createUser - Should return 409 when user already exists")
    void createUserAlreadyExists() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.CONFLICT.value(), "Conflict", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("existente@email.com");
        user.setPassword("Password123!");
        user.setFirstName("Existing");
        user.setLastName("User");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("createUser - Should return 400 if request body is invalid")
    void createUserInvalidRequest() throws Exception {
        String invalidJson = objectMapper.writeValueAsString(Map.of("username", "invalid"));

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("createUser - Should return 401 if token is invalid during user creation")
    void createUserInvalidToken() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("no.token@email.com");
        user.setPassword("Password123!");
        user.setFirstName("No");
        user.setLastName("Token");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("createUser - Should return 403 if not allowed to create user")
    void createUserNoPermission() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("restrict@email.com");
        user.setPassword("Password123!");
        user.setFirstName("Restrict");
        user.setLastName("User");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer restrict-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("createUser - Should return 500 when an unexpected error occurs during creation")
    void createUserInternalError() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("erro@email.com");
        user.setPassword("Pass123!");
        user.setFirstName("Internal");
        user.setLastName("Error");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer any-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ================================
     *     GET USER BY ID TESTS
     * ================================
     */
    @Test
    @DisplayName("getUserById - Should return 200 with user data when fetching by valid ID")
    void getUserByIdSuccess() throws Exception {
        UserResponse userData = new UserResponse();
        userData.setId("123");
        userData.setUsername("user@email.com");
        userData.setFirstName("John");
        userData.setLastName("Doe");
        userData.setEnabled(true);

        Mockito.when(keycloakService.getUserById(anyString(), anyString()))
                .thenReturn(userData);

        mockMvc.perform(get("/api/users/123")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user@email.com"));
    }

    @Test
    @DisplayName("getUserById - Should return 401 if token is invalid when fetching by ID")
    void getUserByIdInvalidToken() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/123")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getUserById - Should return 403 if not allowed to fetch by ID")
    void getUserByIdNoPermission() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/123")
                        .header("Authorization", "Bearer no-permission-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("getUserById - Should return 404 if user is not found")
    void getUserByIdNotFound() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/does-no-exists-id")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getUserById - Should return 500 when an unexpected error occurs during ID search")
    void getUserByIdInternalError() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/error")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ===============================
     *     USER UPDATE TESTS
     * ===============================
     */
    @Test
    @DisplayName("updateUser - Should return 200 when user is successfully updated")
    void updateUserSuccess() throws Exception {
        Mockito.doNothing().when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("user@email.com");
        user.setPassword("NewPassword123");
        user.setFirstName("Updated");
        user.setLastName("User");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @DisplayName("updateUser - Should return 404 if user to update is not found")
    void updateUserNotFound() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("doesntexist@email.com");
        user.setPassword("Pass123!");
        user.setFirstName("Doesnt");
        user.setLastName("Exist");

        mockMvc.perform(put("/api/users/does-not-exists")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("updateUser - Should return 401 if token is invalid during update")
    void updateUserInvalidToken() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("user@email.com");
        user.setPassword("Pass123!");
        user.setFirstName("Test");
        user.setLastName("User");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("updateUser - Should return 403 if not allowed to update")
    void updateUserNoPermission() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("user@email.com");
        user.setPassword("Pass123!");
        user.setFirstName("No");
        user.setLastName("Permission");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer token-without-permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("updateUser - Should return 500 when an unexpected error occurs during update")
    void updateUserInternalError() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected Error"))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("error@email.com");
        user.setPassword("Pass123!");
        user.setFirstName("Internal");
        user.setLastName("Error");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer any-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ==============================
     *     PASSWORD UPDATE TESTS
     * ==============================
     */
    @Test
    @DisplayName("passwordUpdate - Should return 200 when password is successfully updated")
    void updatePasswordSuccess() throws Exception {
        Mockito.doNothing().when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "newPassword123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));
    }

    @Test
    @DisplayName("passwordUpdate - Should return 400 if 'password' field is missing or blank")
    void updatePasswordInvalidField() throws Exception {
        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("passwordUpdate - Should return 401 if token is invalid during password update")
    void updatePasswordInvalidToken() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NewPass123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("passwordUpdate - Should return 403 if not allowed to change password")
    void updatePasswordNoPermission() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer no-permission-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NewPass123"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("passwordUpdate - Should return 404 if user is not found when updating password")
    void updatePasswordNotFound() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/does-not-exists")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NewPass123"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("passwordUpdate - Should return 500 when an unexpected error occurs during password update")
    void updatePasswordInternalError() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer any-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NewPass123"))))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ============================
     *     USER DELETION TESTS
     * ============================
     */
    @Test
    @DisplayName("deleteUser - Should return 200 when user is successfully removed")
    void deleteUserSuccess() throws Exception {
        Mockito.doNothing().when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @DisplayName("deleteUser - Should return 401 if token is invalid during user removal")
    void deleteUserInvalidToken() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("deleteUser - Should return 403 if not allowed to remove user")
    void deleteUserNoPermission() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer no-permission-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deleteUser - Should return 404 if user is not found during removal")
    void deleteUserNotFound() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/does-not-exists")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deleteUser - Should return 500 when an unexpected error occurs during removal")
    void deleteUserInternalError() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer any-token"))
                .andExpect(status().isInternalServerError());
    }
}
