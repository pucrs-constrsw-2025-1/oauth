package com.constrsw.oauth.domain.service;

import com.constrsw.oauth.domain.entity.Role;

import java.util.List;

public interface RoleManagementService {
    
    String createRole(Role role);
    
    List<Role> getAllRoles();
    
    Role getRoleById(String id);
    
    Role getRoleByName(String name);
    
    void updateRole(String id, Role role);
    
    void deleteRole(String id);
}