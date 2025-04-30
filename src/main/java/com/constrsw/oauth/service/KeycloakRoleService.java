package com.constrsw.oauth.service;

import lombok.AllArgsConstructor;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

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

    public RoleRepresentation getRoleById(String roleId) {
        return rolesResource.list().stream()
                .filter(role -> role.getId().equals(roleId))
                .findFirst().orElse(null);
    }

    public void updateRole(String roleName, String newRoleName, String description) {
            RoleResource roleResource = rolesResource.get(roleName);
            RoleRepresentation role = roleResource.toRepresentation();

            if (newRoleName != null && !newRoleName.isEmpty()) {
                role.setName(newRoleName);
            }

            role.setDescription(description);

            roleResource.update(role);
    }

    public void patchRole(String roleName, Map<String, Object> updates) {
            RoleResource roleResource = rolesResource.get(roleName);
            RoleRepresentation role = roleResource.toRepresentation();

            if (updates.containsKey("name")) {
                role.setName((String) updates.get("name"));
            }

            if (updates.containsKey("description")) {
                role.setDescription((String) updates.get("description"));
            }

            roleResource.update(role);
    }

    public void deleteRole(String roleName) {
            rolesResource.deleteRole(roleName);
    }

    public void assignRoleToUser(String userId, String roleName) {
            RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));
    }

    public void removeRoleFromUser(String userId, String roleName) {
            RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
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