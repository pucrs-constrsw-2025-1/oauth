package com.constrsw.oauth.interfaces.mapper;

import com.constrsw.oauth.application.dto.role.RoleRequest;
import com.constrsw.oauth.application.dto.role.RoleResponse;
import com.constrsw.oauth.domain.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    
    public Role toEntity(RoleRequest roleRequest) {
        if (roleRequest == null) {
            return null;
        }
        
        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setComposite(false); // Simple roles by default
        
        return role;
    }
    
    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }
        
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(role.getId());
        roleResponse.setName(role.getName());
        roleResponse.setDescription(role.getDescription());
        roleResponse.setComposite(role.isComposite());
        
        return roleResponse;
    }
    
    public Role partialUpdate(Role existingRole, RoleRequest roleRequest) {
        if (roleRequest == null) {
            return existingRole;
        }
        
        if (roleRequest.getName() != null && !roleRequest.getName().isEmpty()) {
            existingRole.setName(roleRequest.getName());
        }
        
        if (roleRequest.getDescription() != null) {
            existingRole.setDescription(roleRequest.getDescription());
        }
        
        return existingRole;
    }
}