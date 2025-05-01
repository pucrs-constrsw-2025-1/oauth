package com.constrsw.oauth.service;

import com.constrsw.oauth.config.KeycloakConfig;
import com.constrsw.oauth.dto.UserRequest;
import com.constrsw.oauth.dto.UserResponse;
import com.constrsw.oauth.exception.GlobalException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de usuários no Keycloak
 */
@Service
@Slf4j
public class UserService {

    private final Keycloak keycloak;
    private final String realm;

    public UserService(Keycloak keycloak, KeycloakConfig keycloakConfig) {
        this.keycloak = keycloak;
        this.realm = keycloakConfig.getRealm();
    }

    /**
     * Cria um novo usuário
     *
     * @param userRequest Dados do usuário a ser criado
     * @return Dados do usuário criado
     * @throws GlobalException Se ocorrer um erro na criação
     */
    public UserResponse createUser(UserRequest userRequest) {
        try {
            UsersResource usersResource = getUsersResource();

            // Verifica se o usuário já existe
            List<UserRepresentation> existingUsers = usersResource.search(userRequest.getUsername(), true);
            if (!existingUsers.isEmpty()) {
                throw new GlobalException(
                    "USER_EXISTS",
                    "Usuário já existe",
                    "UserService",
                    HttpStatus.CONFLICT
                );
            }

            // Cria a representação do usuário
            UserRepresentation user = new UserRepresentation();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getUsername());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(true);

            // Cria o usuário
            Response response = usersResource.create(user);
            if (response.getStatus() != 201) {
                throw new GlobalException(
                    "CREATE_USER_FAILED",
                    "Falha ao criar usuário",
                    "UserService",
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            // Obtém o ID do usuário criado
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            // Define a senha
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(userRequest.getPassword());
            credential.setTemporary(false);
            usersResource.get(userId).resetPassword(credential);

            // Retorna o usuário criado
            return getUserById(userId);
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage());
            throw new GlobalException(
                "USER_CREATION_ERROR",
                "Erro ao criar usuário: " + e.getMessage(),
                "UserService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Recupera todos os usuários
     *
     * @param enabled Filtro por status (ativo/inativo)
     * @return Lista de usuários
     */
    public List<UserResponse> getAllUsers(Boolean enabled) {
        try {
            UsersResource usersResource = getUsersResource();
            List<UserRepresentation> users = enabled != null ? 
                usersResource.search(null, null, null, null, enabled, null, null, enabled, enabled) : 
                usersResource.list();

            return users.stream()
                    .map(this::mapToUserResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao recuperar usuários: {}", e.getMessage());
            throw new GlobalException(
                "GET_USERS_ERROR",
                "Erro ao recuperar usuários: " + e.getMessage(),
                "UserService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Recupera todos os usuários sem filtragem
     * 
     * @return Lista de usuários
     */
    public List<UserResponse> getAllUsers() {
        return getAllUsers(null);
    }

    /**
     * Recupera um usuário pelo ID
     *
     * @param id ID do usuário
     * @return Dados do usuário
     */
    public UserResponse getUserById(String id) {
        try {
            UserRepresentation user = getUsersResource().get(id).toRepresentation();
            return mapToUserResponse(user);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "USER_NOT_FOUND",
                "Usuário não encontrado com id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Atualiza os dados de um usuário
     *
     * @param id ID do usuário
     * @param userRequest Novos dados do usuário
     */
    public void updateUser(String id, UserRequest userRequest) {
        try {
            UserResource userResource = getUsersResource().get(id);
            UserRepresentation user = userResource.toRepresentation();
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "USER_NOT_FOUND",
                "Usuário não encontrado com id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Atualiza a senha de um usuário
     *
     * @param id ID do usuário
     * @param newPassword Nova senha
     */
    public void updatePassword(String id, String newPassword) {
        try {
            UserResource userResource = getUsersResource().get(id);
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);
            userResource.resetPassword(credential);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "USER_NOT_FOUND",
                "Usuário não encontrado com id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Desativa um usuário
     *
     * @param id ID do usuário
     */
    public void disableUser(String id) {
        try {
            UserResource userResource = getUsersResource().get(id);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(false);
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "USER_NOT_FOUND",
                "Usuário não encontrado com id: " + id,
                "UserService",
                HttpStatus.NOT_FOUND
            );
        }
    }
    /**
     * Busca usuários pelo nome de usuário
     *
     * @param username Nome de usuário (email)
     * @return Lista de usuários encontrados
     */
    public List<UserResponse> getUserByUsername(String username) {
        try {
            UsersResource usersResource = getUsersResource();
            List<UserRepresentation> users = usersResource.search(username, true);
            
            log.info("Encontrados {} usuários com username: {}", users.size(), username);
            
            return users.stream()
                    .map(this::mapToUserResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por username {}: {}", username, e.getMessage(), e);
            throw new GlobalException(
                "GET_USER_ERROR",
                "Erro ao buscar usuário: " + e.getMessage(),
                "UserService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtém o recurso de usuários do Keycloak
     *
     * @return UsersResource
     */
    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    /**
     * Mapeia a representação do usuário para o DTO de resposta
     *
     * @param user Representação do usuário
     * @return DTO de resposta
     */
    private UserResponse mapToUserResponse(UserRepresentation user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.isEnabled())
                .build();
    }
}