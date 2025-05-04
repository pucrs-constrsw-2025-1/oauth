package com.constrsw.oauth.application.service;

import com.constrsw.oauth.application.dto.role.RoleRequest;
import com.constrsw.oauth.application.dto.role.RoleResponse;
import com.constrsw.oauth.domain.entity.Role;
import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.service.RoleManagementService;
import com.constrsw.oauth.interfaces.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    
    private final RoleManagementService roleManagementService;
    private final RoleMapper roleMapper;
    
    @Autowired
    public RoleService(RoleManagementService roleManagementService, RoleMapper roleMapper) {
        this.roleManagementService = roleManagementService;
        this.roleMapper = roleMapper;
    }
    
    public RoleResponse createRole(RoleRequest roleRequest) {
        try {
            Role role = roleMapper.toEntity(roleRequest);
            String roleId = roleManagementService.createRole(role);
            Role createdRole = roleManagementService.getRoleById(roleId);
            return roleMapper.toResponse(createdRole);
        } catch (Exception e) {
            throw new DomainException("OA-201", "Role creation failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public List<RoleResponse> getAllRoles() {
        try {
            List<Role> roles = roleManagementService.getAllRoles();
            return roles.stream()
                    .map(roleMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DomainException("OA-202", "Retrieving all roles failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public RoleResponse getRoleById(String id) {
        try {
            Role role = roleManagementService.getRoleById(id);
            return roleMapper.toResponse(role);
        } catch (Exception e) {
            throw new DomainException("OA-203", "Retrieving role by id failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void updateRole(String id, RoleRequest roleRequest) {
        try {
            Role role = roleMapper.toEntity(roleRequest);
            roleManagementService.updateRole(id, role);
        } catch (Exception e) {
            throw new DomainException("OA-204", "Role update failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void patchRole(String id, RoleRequest roleRequest) {
        try {
            Role existingRole = roleManagementService.getRoleById(id);
            Role updatedRole = roleMapper.partialUpdate(existingRole, roleRequest);
            roleManagementService.updateRole(id, updatedRole);
        } catch (Exception e) {
            throw new DomainException("OA-205", "Role patch failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void deleteRole(String id) {
        try {
            roleManagementService.deleteRole(id);
        } catch (Exception e) {
            throw new DomainException("OA-206", "Role deletion failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
}