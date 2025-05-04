package com.constrsw.oauth.domain.repository;

import com.constrsw.oauth.domain.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    
    String createRole(Role role);
    
    List<Role> findAllRoles();
    
    Optional<Role> findRoleById(String id);
    
    Optional<Role> findRoleByName(String name);
    
    void updateRole(String id, Role role);
    
    void deleteRole(String id);
}