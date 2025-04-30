package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.RoleRequest;
import com.constrsw.oauth.dto.RoleResponse;
import com.constrsw.oauth.exception.GlobalException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public RoleResponse createRole(RoleRequest roleRequest) {
        try {
            RolesResource rolesResource = getRolesResource();

            // Check if role exists
            try {
                RoleRepresentation existingRole = rolesResource.get(roleRequest.getName()).toRepresentation();
                if (existingRole != null) {
                    throw new GlobalException(
                        "ROLE_EXISTS",
                        "Role with name '" + roleRequest.getName() + "' already exists",
                        "RoleService",
                        HttpStatus.CONFLICT
                    );
                }
            } catch (NotFoundException e) {
                // Role doesn't exist, we can continue
            }

            // Create role
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleRequest.getName());
            role.setDescription(roleRequest.getDescription());

            rolesResource.create(role);

            // Retrieve created role to get its ID
            RoleRepresentation createdRole = rolesResource.get(roleRequest.getName()).toRepresentation();
            return mapToRoleResponse(createdRole);
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            throw new GlobalException(
                "ROLE_CREATION_ERROR",
                "Error creating role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public List<RoleResponse> getAllRoles() {
        try {
            List<RoleRepresentation> roles = getRolesResource().list();
            return roles.stream()
                    .map(this::mapToRoleResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new GlobalException(
                "GET_ROLES_ERROR",
                "Error fetching roles: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public RoleResponse getRoleById(String id) {
        try {
            List<RoleRepresentation> roles = getRolesResource().list();
            RoleRepresentation role = roles.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));

            return mapToRoleResponse(role);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                "Role not found with id: " + id,
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new GlobalException(
                "GET_ROLE_ERROR",
                "Error fetching role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public void updateRole(String id, RoleRequest roleRequest) {
        try {
            List<RoleRepresentation> roles = getRolesResource().list();
            RoleRepresentation role = roles.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));

            RoleResource roleResource = getRolesResource().get(role.getName());
            role.setDescription(roleRequest.getDescription());
            
            // If the name is changing, we need to delete and recreate the role
            if (!role.getName().equals(roleRequest.getName())) {
                // Check if the new name already exists
                try {
                    RoleRepresentation existingRole = getRolesResource().get(roleRequest.getName()).toRepresentation();
                    if (existingRole != null) {
                        throw new GlobalException(
                            "ROLE_EXISTS",
                            "Role with name '" + roleRequest.getName() + "' already exists",
                            "RoleService",
                            HttpStatus.CONFLICT
                        );
                    }
                } catch (NotFoundException e) {
                    // Role with new name doesn't exist, we can continue
                }
                
                // Create new role with updated name
                RoleRepresentation newRole = new RoleRepresentation();
                newRole.setName(roleRequest.getName());
                newRole.setDescription(roleRequest.getDescription());
                
                // Delete old role
                roleResource.remove();
                
                // Create new role
                getRolesResource().create(newRole);
            } else {
                // Just update the existing role
                roleResource.update(role);
            }
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                "Role not found with id: " + id,
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            throw new GlobalException(
                "UPDATE_ROLE_ERROR",
                "Error updating role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public void patchRole(String id, RoleRequest roleRequest) {
        try {
            List<RoleRepresentation> roles = getRolesResource().list();
            RoleRepresentation role = roles.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));

            RoleResource roleResource = getRolesResource().get(role.getName());
            
            // Update only the provided fields
            if (roleRequest.getDescription() != null) {
                role.setDescription(roleRequest.getDescription());
            }
            
            roleResource.update(role);
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                "Role not found with id: " + id,
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new GlobalException(
                "PATCH_ROLE_ERROR",
                "Error patching role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public void deleteRole(String id) {
        try {
            List<RoleRepresentation> roles = getRolesResource().list();
            RoleRepresentation role = roles.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));

            getRolesResource().get(role.getName()).remove();
        } catch (NotFoundException e) {
            throw new GlobalException(
                "ROLE_NOT_FOUND",
                "Role not found with id: " + id,
                "RoleService",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new GlobalException(
                "DELETE_ROLE_ERROR",
                "Error deleting role: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public void assignRolesToUser(String userId, List<String> roleIds) {
        try {
            // Get user
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource == null) {
                throw new GlobalException(
                    "USER_NOT_FOUND",
                    "User not found with id: " + userId,
                    "RoleService",
                    HttpStatus.NOT_FOUND
                );
            }

            // Get roles
            List<RoleRepresentation> allRoles = getRolesResource().list();
            List<RoleRepresentation> rolesToAssign = new ArrayList<>();
            
            for (String roleId : roleIds) {
                RoleRepresentation role = allRoles.stream()
                        .filter(r -> r.getId().equals(roleId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
                rolesToAssign.add(role);
            }
            
            // Assign roles to user
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
            throw new GlobalException(
                "ASSIGN_ROLES_ERROR",
                "Error assigning roles to user: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public void removeRolesFromUser(String userId, List<String> roleIds) {
        try {
            // Get user
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource == null) {
                throw new GlobalException(
                    "USER_NOT_FOUND",
                    "User not found with id: " + userId,
                    "RoleService",
                    HttpStatus.NOT_FOUND
                );
            }

            // Get roles
            List<RoleRepresentation> allRoles = getRolesResource().list();
            List<RoleRepresentation> rolesToRemove = new ArrayList<>();
            
            for (String roleId : roleIds) {
                RoleRepresentation role = allRoles.stream()
                        .filter(r -> r.getId().equals(roleId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));
                rolesToRemove.add(role);
            }
            
            // Remove roles from user
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
            throw new GlobalException(
                "REMOVE_ROLES_ERROR",
                "Error removing roles from user: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    public List<RoleResponse> getUserRoles(String userId) {
        try {
            // Get user
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            if (userResource == null) {
                throw new GlobalException(
                    "USER_NOT_FOUND",
                    "User not found with id: " + userId,
                    "RoleService",
                    HttpStatus.NOT_FOUND
                );
            }

            // Get user roles
            List<RoleRepresentation> userRoles = userResource.roles().realmLevel().listAll();
            
            return userRoles.stream()
                    .map(this::mapToRoleResponse)
                    .collect(Collectors.toList());
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            throw new GlobalException(
                "GET_USER_ROLES_ERROR",
                "Error fetching user roles: " + e.getMessage(),
                "RoleService",
                HttpStatus.INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    private RolesResource getRolesResource() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.roles();
    }

    private RoleResponse mapToRoleResponse(RoleRepresentation role) {
        // Verificação segura para clientRole
        boolean isClientRole = false;
        try {
            // Alternativa para verificar se é uma role de cliente
            isClientRole = role.getClientRole() != null && role.getClientRole();
        } catch (Exception e) {
            log.warn("Could not determine role type for role {}", role.getName(), e);
        }
    
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .composite(role.isComposite())
                .clientRole(isClientRole)
                .containerId(role.getContainerId() != null ? role.getContainerId() : "")
                .build();
    }
}