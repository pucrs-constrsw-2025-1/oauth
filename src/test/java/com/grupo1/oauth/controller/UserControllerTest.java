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
import java.util.List;
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
     *     TESTES DE LOGIN
     * ========================
     */
    @Test
    @DisplayName("Deve retornar 201 ao realizar login com sucesso")
    void loginComSucesso() throws Exception {
        TokenResponse token = new TokenResponse();
        token.setAccessToken("fake-token");
        token.setExpiresIn(300L);
        token.setRefreshToken("refresh-token");
        token.setTokenType("bearer");

        Mockito.when(keycloakService.login(any(LoginRequest.class))).thenReturn(token);

        LoginRequest request = new LoginRequest();
        request.setUsername("usuario");
        request.setPassword("senha");

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("fake-token"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o body estiver malformado")
    void loginComErro400() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content("{\"username\":\"usuario\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 401 ao usar credenciais inválidas")
    void loginComErro401() throws Exception {
        Mockito.when(keycloakService.login(any(LoginRequest.class)))
                .thenThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        null, null, StandardCharsets.UTF_8));

        LoginRequest request = new LoginRequest();
        request.setUsername("usuario");
        request.setPassword("senhaErrada");

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro inesperado")
    void loginComErro500() throws Exception {
        Mockito.when(keycloakService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Erro interno"));

        LoginRequest request = new LoginRequest();
        request.setUsername("usuario");
        request.setPassword("senha");

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ============================
     *   TESTES DE LISTAGEM DE USUÁRIOS
     * ============================
     */

    @Test
    @DisplayName("Deve retornar 200 com lista de usuários se token for válido")
    void getUsersComSucesso() throws Exception {
        UserResponse user1 = new UserResponse();
        user1.setId("1");
        user1.setUsername("user1@email.com");

        UserResponse user2 = new UserResponse();
        user2.setId("2");
        user2.setUsername("user2@email.com");

        List<UserResponse> users = List.of(user1, user2);

        Mockito.when(keycloakService.getUsers(anyString())).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer token-valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 401 se token for inválido na listagem de usuários")
    void getUsersTokenInvalido() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUsers(anyString());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 se não tiver permissão para listar usuários")
    void getUsersSemPermissao() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUsers(anyString());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer token-sem-permissao"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro inesperado na listagem de usuários")
    void getUsersErroInterno() throws Exception {
        Mockito.doThrow(new RuntimeException("Erro inesperado"))
                .when(keycloakService).getUsers(anyString());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer qualquer-token"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ==============================
     *   TESTES DE CRIAÇÃO DE USUÁRIO
     * ==============================
     */

    @Test
    @DisplayName("Deve retornar 201 ao criar usuário com sucesso")
    void createUserComSucesso() throws Exception {
        // mocka o comportamento (não retorna nada, apenas não lança exceção)
        Mockito.doNothing().when(keycloakService).createUser(any(UserRequest.class), any(String.class));

        UserRequest user = new UserRequest();
        user.setUsername("novo@teste.com");
        user.setPassword("Senha123!");
        user.setFirstName("Novo");
        user.setLastName("Usuário");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário criado com sucesso"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando o usuário já existe")
    void createUserJaExiste() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.CONFLICT.value(), "Conflict", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("existente@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Existente");
        user.setLastName("Usuário");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve retornar 400 se corpo da requisição estiver inválido")
    void createUserRequisicaoInvalida() throws Exception {
        // falta campos obrigatórios no JSON
        String invalidJson = objectMapper.writeValueAsString(Map.of("username", "invalido"));

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 401 se token for inválido")
    void createUserTokenInvalido() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("sem.token@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Sem");
        user.setLastName("Token");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 se não tiver permissão para criar usuário")
    void createUserSemPermissao() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("restrito@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Restrito");
        user.setLastName("Usuário");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer token-restrito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro inesperado na criação")
    void createUserErroInterno() throws Exception {
        Mockito.doThrow(new RuntimeException("Erro inesperado"))
                .when(keycloakService).createUser(any(UserRequest.class), anyString());

        UserRequest user = new UserRequest();
        user.setUsername("erro@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Erro");
        user.setLastName("Interno");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer qualquer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ================================
     *   TESTES DE BUSCA DE USUÁRIO POR ID
     * ================================
     */

    @Test
    @DisplayName("Deve retornar 200 com os dados do usuário ao buscar por ID válido")
    void getUserByIdComSucesso() throws Exception {
        UserResponse userData = new UserResponse();
        userData.setId("123");
        userData.setUsername("usuario@email.com");
        userData.setFirstName("Nome");
        userData.setLastName("Sobrenome");
        userData.setEnabled(true);

        Mockito.when(keycloakService.getUserById(anyString(), anyString()))
                .thenReturn(userData);

        mockMvc.perform(get("/api/users/123")
                        .header("Authorization", "Bearer token-valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("usuario@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 401 se token for inválido na busca por ID")
    void getUserByIdTokenInvalido() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/123")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 se não tiver permissão para buscar por ID")
    void getUserByIdSemPermissao() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/123")
                        .header("Authorization", "Bearer sem-permissao"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 se o usuário não for encontrado")
    void getUserByIdNaoEncontrado() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/nao-existe")
                        .header("Authorization", "Bearer valido"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro inesperado na busca por ID")
    void getUserByIdErroInterno() throws Exception {
        Mockito.doThrow(new RuntimeException("Erro inesperado"))
                .when(keycloakService).getUserById(anyString(), anyString());

        mockMvc.perform(get("/api/users/erro")
                        .header("Authorization", "Bearer qualquer"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ===============================
     *   TESTES DE ATUALIZAÇÃO DE USUÁRIO
     * ===============================
     */

    @Test
    @DisplayName("Deve retornar 200 ao atualizar usuário com sucesso")
    void updateUserComSucesso() throws Exception {
        Mockito.doNothing().when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("usuario@email.com");
        user.setPassword("NovaSenha123");
        user.setFirstName("Atualizado");
        user.setLastName("Nome");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário atualizado com sucesso"));
    }

    @Test
    @DisplayName("Deve retornar 404 se o usuário para atualizar não for encontrado")
    void updateUserNaoEncontrado() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("naoexiste@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Não");
        user.setLastName("Existe");

        mockMvc.perform(put("/api/users/nao-existe")
                        .header("Authorization", "Bearer valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 401 se token for inválido na atualização")
    void updateUserTokenInvalido() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("usuario@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Teste");
        user.setLastName("TokenInvalido");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 se não tiver permissão para atualizar")
    void updateUserSemPermissao() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("usuario@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Sem");
        user.setLastName("Permissão");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer token-sem-permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro inesperado na atualização")
    void updateUserErroInterno() throws Exception {
        Mockito.doThrow(new RuntimeException("Erro inesperado"))
                .when(keycloakService).updateUser(anyString(), anyString(), any(UserRequest.class));

        UserRequest user = new UserRequest();
        user.setUsername("erro@email.com");
        user.setPassword("Senha123!");
        user.setFirstName("Erro");
        user.setLastName("Interno");

        mockMvc.perform(put("/api/users/123")
                        .header("Authorization", "Bearer qualquer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ==============================
     *   TESTES DE ATUALIZAÇÃO DE SENHA
     * ==============================
     */

    @Test
    @DisplayName("Deve retornar 200 ao atualizar a senha com sucesso")
    void updatePasswordComSucesso() throws Exception {
        Mockito.doNothing().when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NovaSenha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Senha atualizada com sucesso"));
    }

    @Test
    @DisplayName("Deve retornar 400 se o campo 'password' estiver ausente ou em branco")
    void updatePasswordCampoInvalido() throws Exception {
        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 401 se token for inválido na atualização de senha")
    void updatePasswordTokenInvalido() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NovaSenha123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 se não tiver permissão para alterar senha")
    void updatePasswordSemPermissao() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer token-sem-permissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NovaSenha123"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 se o usuário não for encontrado ao alterar senha")
    void updatePasswordNaoEncontrado() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/nao-existe")
                        .header("Authorization", "Bearer token-valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NovaSenha123"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro inesperado na atualização de senha")
    void updatePasswordErroInterno() throws Exception {
        Mockito.doThrow(new RuntimeException("Erro inesperado"))
                .when(keycloakService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/123")
                        .header("Authorization", "Bearer qualquer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "NovaSenha123"))))
                .andExpect(status().isInternalServerError());
    }

    /**
     * ============================
     *   TESTES DE REMOÇÃO DE USUÁRIO
     * ============================
     */

    @Test
    @DisplayName("Deve retornar 200 ao remover usuário com sucesso")
    void deleteUserComSucesso() throws Exception {
        Mockito.doNothing().when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer token-valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário removido com sucesso"));
    }

    @Test
    @DisplayName("Deve retornar 401 se token for inválido na remoção de usuário")
    void deleteUserTokenInvalido() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 403 se não tiver permissão para remover usuário")
    void deleteUserSemPermissao() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer token-sem-permissao"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 se o usuário não for encontrado na remoção")
    void deleteUserNaoEncontrado() throws Exception {
        Mockito.doThrow(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", null, null, StandardCharsets.UTF_8))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/nao-existe")
                        .header("Authorization", "Bearer token-valido"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro inesperado na remoção")
    void deleteUserErroInterno() throws Exception {
        Mockito.doThrow(new RuntimeException("Erro inesperado"))
                .when(keycloakService).disableUser(anyString(), anyString());

        mockMvc.perform(delete("/api/users/123")
                        .header("Authorization", "Bearer qualquer-token"))
                .andExpect(status().isInternalServerError());
    }
}
