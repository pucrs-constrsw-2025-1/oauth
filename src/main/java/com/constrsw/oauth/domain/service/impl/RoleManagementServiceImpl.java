package com.constrsw.oauth.domain.service.impl;

import com.constrsw.oauth.domain.entity.Role;
import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.repository.RoleRepository;
import com.constrsw.oauth.domain.service.RoleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleManagementServiceImpl implements RoleManagementService {
    
    private final RoleRepository roleRepository;
    
    @Autowired
    public RoleManagementServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public String createRole(Role role) {
        validateRoleForCreation(role);
        
        // Check if a role with the same name already exists
        roleRepository.findRoleByName(role.getName()).ifPresent(existingRole -> {
            throw new DomainException("OA-409", "Role with name '" + role.getName() + "' already exists", "OAuthAPI");
        });
        
        return roleRepository.createRole(role);
    }
    
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAllRoles();
    }
    
    @Override
    public Role getRoleById(String id) {
        return roleRepository.findRoleById(id)
                .orElseThrow(() -> new DomainException("OA-404", "Role not found with id: " + id, "OAuthAPI"));
    }
    
    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findRoleByName(name)
                .orElseThrow(() -> new DomainException("OA-404", "Role not found with name: " + name, "OAuthAPI"));
    }
    
    @Override
    public void updateRole(String id, Role role) {
        validateRoleForUpdate(role);
        
        // Verify if the role exists
        Role existingRole = roleRepository.findRoleById(id)
                .orElseThrow(() -> new DomainException("OA-404", "Role not found with id: " + id, "OAuthAPI"));
        
        // If the name is changed, check if another role with the new name already exists
        if (!existingRole.getName().equals(role.getName())) {
            roleRepository.findRoleByName(role.getName()).ifPresent(anotherRole -> {
                throw new DomainException("OA-409", "Another role with name '" + role.getName() + "' already exists", "OAuthAPI");
            });
        }
        
        roleRepository.updateRole(id, role);
    }
    
    @Override
    public void deleteRole(String id) {
        // Verify if the role exists
        roleRepository.findRoleById(id)
                .orElseThrow(() -> new DomainException("OA-404", "Role not found with id: " + id, "OAuthAPI"));
        
        roleRepository.deleteRole(id);
    }
    
    private void validateRoleForCreation(Role role) {
        if (role == null) {
            throw new DomainException("OA-400", "Role cannot be null", "OAuthAPI");
        }
        
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new DomainException("OA-400", "Role name cannot be empty", "OAuthAPI");
        }
    }
    
    private void validateRoleForUpdate(Role role) {
        if (role == null) {
            throw new DomainException("OA-400", "Role cannot be null", "OAuthAPI");
        }
        
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new DomainException("OA-400", "Role name cannot be empty", "OAuthAPI");
        }
    }
}