package com.constrsw.oauth.service;

import lombok.AllArgsConstructor;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class KeycloakRoleService {

    private final RolesResource rolesResource;
    private final UsersResource usersResource;

    public void createRole(String roleName, String description) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        role.setDescription(description);
        role.setComposite(false);

        rolesResource.create(role);
    }

    public List<RoleRepresentation> getAllRoles() {
        return rolesResource.list();
    }

    public RoleRepresentation getRoleById(String id) {
        return rolesResource.list().stream()
                .filter(role -> role.getId().equals(id))
                .findFirst().orElse(null);
    }

    public void updateRole(String id, String newRoleName, String description) {
            RoleRepresentation role = getRoleById(id);
            if (role == null) {
                throw new NotFoundException("Role não encontrada com o ID: " + id);
            }
            
            role.setName(newRoleName);
            role.setDescription(description);
            
            rolesResource.get(role.getName()).update(role);
    }

    public void patchRole(String id, Map<String, Object> updates) {
            RoleRepresentation role = getRoleById(id);
            if (role == null) {
                throw new NotFoundException("Role não encontrada com o ID: " + id);
            }

            if (updates.containsKey("name")) {
                role.setName((String) updates.get("name"));
            }

            if (updates.containsKey("description")) {
                role.setDescription((String) updates.get("description"));
            }

            rolesResource.get(role.getName()).update(role);
    }

    public void deleteRole(String id) {
            RoleRepresentation role = getRoleById(id);
            rolesResource.deleteRole(role.getName());
    }

    public void assignRoleToUser(String userId, String id) {
            RoleRepresentation role = getRoleById(id);
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));
    }

    public void removeRoleFromUser(String userId, String id) {
            RoleRepresentation role = getRoleById(id);
            usersResource.get(userId).roles().realmLevel().remove(Collections.singletonList(role));
    }

    public List<RoleRepresentation> getUserRoles(String userId) {
        return usersResource.get(userId).roles().realmLevel().listAll();
    }

    public void assignRolesToUser(String userId, List<String> roleNames) {
            List<RoleRepresentation> roles = roleNames.stream()
                    .map(name -> rolesResource.get(name).toRepresentation())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            usersResource.get(userId).roles().realmLevel().add(roles);
    }

    public void removeRolesFromUser(String userId, List<String> roleNames) {
        List<RoleRepresentation> roles = roleNames.stream()
                .map(name -> rolesResource.get(name).toRepresentation())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        usersResource.get(userId).roles().realmLevel().remove(roles);
    }
}