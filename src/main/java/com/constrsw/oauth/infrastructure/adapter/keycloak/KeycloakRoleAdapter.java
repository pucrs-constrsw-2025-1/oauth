package com.constrsw.oauth.infrastructure.adapter.keycloak;

import com.constrsw.oauth.domain.entity.Role;
import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.repository.RoleRepository;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class KeycloakRoleAdapter implements RoleRepository {
    
    private final Keycloak keycloak;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Autowired
    public KeycloakRoleAdapter(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    
    
    @Override
    public String createRole(Role role) {
        try {
            RoleRepresentation roleRepresentation = mapToKeycloakRole(role);
            keycloak.realm(realm).roles().create(roleRepresentation);
            
            // Get the created role to retrieve its ID
            RoleRepresentation createdRole = keycloak.realm(realm).roles().get(role.getName()).toRepresentation();
            return createdRole.getId();
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to create role: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public List<Role> findAllRoles() {
        try {
            List<RoleRepresentation> roleRepresentations = keycloak.realm(realm).roles().list();
            return roleRepresentations.stream()
                    .map(this::mapToDomainRole)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to retrieve roles: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public Optional<Role> findRoleById(String id) {
        try {
            // Keycloak doesn't provide a direct way to get a role by ID
            // We need to get all roles and find the one with the matching ID
            List<RoleRepresentation> roles = keycloak.realm(realm).roles().list();
            return roles.stream()
                    .filter(role -> role.getId().equals(id))
                    .findFirst()
                    .map(this::mapToDomainRole);
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to retrieve role by id: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public Optional<Role> findRoleByName(String name) {
        try {
            RoleRepresentation roleRepresentation = keycloak.realm(realm).roles().get(name).toRepresentation();
            return Optional.of(mapToDomainRole(roleRepresentation));
        } catch (NotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to retrieve role by name: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void updateRole(String id, Role role) {
        try {
            Optional<Role> existingRole = findRoleById(id);
            if (existingRole.isPresent()) {
                RoleRepresentation roleRepresentation = mapToKeycloakRole(role);
                // Keycloak updates roles by name, not by ID
                keycloak.realm(realm).roles().get(existingRole.get().getName()).update(roleRepresentation);
            } else {
                throw new DomainException("OA-404", "Role not found with id: " + id, "KeycloakAPI");
            }
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to update role: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    @Override
    public void deleteRole(String id) {
        try {
            Optional<Role> existingRole = findRoleById(id);
            if (existingRole.isPresent()) {
                keycloak.realm(realm).roles().deleteRole(existingRole.get().getName());
            } else {
                throw new DomainException("OA-404", "Role not found with id: " + id, "KeycloakAPI");
            }
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("OA-500", "Failed to delete role: " + e.getMessage(), "KeycloakAPI", e);
        }
    }
    
    private RoleRepresentation mapToKeycloakRole(Role role) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role.getName());
        roleRepresentation.setDescription(role.getDescription());
        roleRepresentation.setComposite(role.isComposite());
        
        return roleRepresentation;
    }
    
    private Role mapToDomainRole(RoleRepresentation roleRepresentation) {
        Role role = new Role();
        role.setId(roleRepresentation.getId());
        role.setName(roleRepresentation.getName());
        role.setDescription(roleRepresentation.getDescription());
        role.setComposite(roleRepresentation.isComposite());
        
        return role;
    }
}