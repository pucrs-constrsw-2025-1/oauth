package com.constrsw.oauth.domain.service;

import com.constrsw.oauth.domain.entity.User;

import java.util.List;

public interface UserManagementService {
    
    String createUser(User user);
    
    List<User> getAllUsers();
    
    List<User> getUsersByEnabled(boolean enabled);
    
    User getUserById(String id);
    
    void updateUser(String id, User user);
    
    void updateUserPassword(String id, String password);
    
    void disableUser(String id);
    
    void addRoleToUser(String userId, String roleId);
    
    void removeRoleFromUser(String userId, String roleId);
}