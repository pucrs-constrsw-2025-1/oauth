package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.RoleRequest;
import com.constrsw.oauth.dto.RoleResponse;
import com.constrsw.oauth.exception.GlobalException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de roles no Keycloak
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    /**
     * Cria uma nova role
     *
     * @param roleRequest Dados da role a ser criada
     * @return Dados da role criada
     */
    public RoleResponse createRole(RoleRequest roleRequest) {
        try {
            RolesResource rolesResource = getRolesResource();

            // Verifica se a role já existe
            try {
                RoleRepresentation existingRole = rolesResource.get(roleRequest.getName()).toRepresentation();
                if (existingRole != null) {
                    throw new GlobalException(
                        "ROLE_EXISTS",
                        "Role já existe: " + roleRequest.getName(),
                        "RoleService",
                        HttpStatus.CONFLICT
                    );
                }
            } catch (NotFoundException e) {
                // Role não existe, podemos continuar
            }

            // Cria a role
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleRequest.getName());
            role.setDescription(roleRequest.getDescription());
            rolesResource.create(role);

            // Recupera a role criada
            RoleRepresentation createdRole = rolesResource.get(roleRequest.getName()).toRepresentation();
            return mapToRoleResponse(createdRole);
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao criar role: {}", e.getMessage());
            throw new GlobalException(
                "ROLE_CREATION_ERROR",
                "Erro ao criar role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Recupera todas as roles
     *
     * @return Lista de roles
     */
    public List<RoleResponse> getAllRoles() {
        try {
            List<RoleRepresentation> roles = getRolesResource().list();
            return roles.stream()
                    .map(this::mapToRoleResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao recuperar roles: {}", e.getMessage());
            throw new GlobalException(
                "GET_ROLES_ERROR",
                "Erro ao recuperar roles: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Recupera uma role pelo ID
     *
     * @param id ID da role
     * @return Dados da role
     */
    public RoleResponse getRoleById(String id) {
        try {
            List<RoleRepresentation> roles = getRolesResource().list();
            RoleRepresentation role = roles.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Role não encontrada com id: " + id));

            return mapToRoleResponse(role);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                "Role não encontrada com id: " + id,
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Erro ao recuperar role: {}", e.getMessage());
            throw new GlobalException(
                "GET_ROLE_ERROR",
                "Erro ao recuperar role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Atualiza uma role
     *
     * @param id ID da role
     * @param roleRequest Novos dados da role
     */
    public void updateRole(String id, RoleRequest roleRequest) {
        try {
            // Busca a role pelo ID
            List<RoleRepresentation> roles = getRolesResource().list();
            RoleRepresentation role = roles.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Role não encontrada com id: " + id));

            // Atualiza a role
            RoleResource roleResource = getRolesResource().get(role.getName());
            role.setDescription(roleRequest.getDescription());
            
            // Se o nome está mudando, precisamos excluir e recriar
            if (!role.getName().equals(roleRequest.getName())) {
                // Verifica se o novo nome já existe
                try {
                    RoleRepresentation existingRole = getRolesResource().get(roleRequest.getName()).toRepresentation();
                    if (existingRole != null) {
                        throw new GlobalException(
                            "ROLE_EXISTS",
                            "Role já existe com o nome: " + roleRequest.getName(),
                            "RoleService",
                            HttpStatus.CONFLICT
                        );
                    }
                } catch (NotFoundException e) {
                    // Role com novo nome não existe, podemos continuar
                }
                
                // Cria nova role com o nome atualizado
                RoleRepresentation newRole = new RoleRepresentation();
                newRole.setName(roleRequest.getName());
                newRole.setDescription(roleRequest.getDescription());
                
                // Exclui a role antiga
                roleResource.remove();
                
                // Cria a nova role
                getRolesResource().create(newRole);
            } else {
                // Atualiza a role existente
                roleResource.update(role);
            }
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                "Role não encontrada com id: " + id,
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar role: {}", e.getMessage());
            throw new GlobalException(
                "UPDATE_ROLE_ERROR",
                "Erro ao atualizar role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Exclui uma role
     *
     * @param id ID da role
     */
    public void deleteRole(String id) {
        try {
            // Busca a role pelo ID
            List<RoleRepresentation> roles = getRolesResource().list();
            RoleRepresentation role = roles.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Role não encontrada com id: " + id));

            // Exclui a role
            getRolesResource().get(role.getName()).remove();
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                "Role não encontrada com id: " + id,
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Erro ao excluir role: {}", e.getMessage());
            throw new GlobalException(
                "DELETE_ROLE_ERROR",
                "Erro ao excluir role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Atribui roles a um usuário
     *
     * @param userId ID do usuário
     * @param roleIds Lista de IDs de roles
     */
    public void assignRolesToUser(String userId, List<String> roleIds) {
        try {
            // Obtém o usuário
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource == null) {
                throw new GlobalException(
                    "USER_NOT_FOUND",
                    "Usuário não encontrado com id: " + userId,
                    "RoleService",
                    HttpStatus.NOT_FOUND
                );
            }

            // Obtém as roles
            List<RoleRepresentation> allRoles = getRolesResource().list();
            List<RoleRepresentation> rolesToAssign = new ArrayList<>();
            
            for (String roleId : roleIds) {
                RoleRepresentation role = allRoles.stream()
                        .filter(r -> r.getId().equals(roleId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Role não encontrada com id: " + roleId));
                rolesToAssign.add(role);
            }
            
            // Atribui roles ao usuário
            userResource.roles().realmLevel().add(rolesToAssign);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                e.getMessage(),
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atribuir roles ao usuário: {}", e.getMessage());
            throw new GlobalException(
                "ASSIGN_ROLES_ERROR",
                "Erro ao atribuir roles ao usuário: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Remove roles de um usuário
     *
     * @param userId ID do usuário
     * @param roleIds Lista de IDs de roles
     */
    public void removeRolesFromUser(String userId, List<String> roleIds) {
        try {
            // Obtém o usuário
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource == null) {
                throw new GlobalException(
                    "USER_NOT_FOUND",
                    "Usuário não encontrado com id: " + userId,
                    "RoleService",
                    HttpStatus.NOT_FOUND
                );
            }

            // Obtém as roles
            List<RoleRepresentation> allRoles = getRolesResource().list();
            List<RoleRepresentation> rolesToRemove = new ArrayList<>();
            
            for (String roleId : roleIds) {
                RoleRepresentation role = allRoles.stream()
                        .filter(r -> r.getId().equals(roleId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Role não encontrada com id: " + roleId));
                rolesToRemove.add(role);
            }
            
            // Remove roles do usuário
            userResource.roles().realmLevel().remove(rolesToRemove);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                e.getMessage(),
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao remover roles do usuário: {}", e.getMessage());
            throw new GlobalException(
                "REMOVE_ROLES_ERROR",
                "Erro ao remover roles do usuário: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Recupera as roles de um usuário
     *
     * @param userId ID do usuário
     * @return Lista de roles do usuário
     */
    public List<RoleResponse> getUserRoles(String userId) {
        try {
            // Obtém o usuário
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource == null) {
                throw new GlobalException(
                    "USER_NOT_FOUND",
                    "Usuário não encontrado com id: " + userId,
                    "RoleService",
                    HttpStatus.NOT_FOUND
                );
            }

            // Obtém as roles do usuário
            List<RoleRepresentation> userRoles = userResource.roles().realmLevel().listAll();
            
            return userRoles.stream()
                    .map(this::mapToRoleResponse)
                    .collect(Collectors.toList());
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao recuperar roles do usuário: {}", e.getMessage());
            throw new GlobalException(
                "GET_USER_ROLES_ERROR",
                "Erro ao recuperar roles do usuário: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtém o recurso de roles do Keycloak
     *
     * @return RolesResource
     */
    private RolesResource getRolesResource() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.roles();
    }

    /**
     * Mapeia a representação da role para o DTO de resposta
     *
     * @param role Representação da role
     * @return DTO de resposta
     */
    private RoleResponse mapToRoleResponse(RoleRepresentation role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .composite(role.isComposite())
                .clientRole(role.isClientRole())
                .containerId(role.getContainerId())
                .build();
    }
}