package com.constrsw.oauth.domain.repository;

import com.constrsw.oauth.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    
    String createUser(User user);
    
    List<User> findAllUsers();
    
    List<User> findUsersByEnabled(boolean enabled);
    
    Optional<User> findUserById(String id);
    
    void updateUser(String id, User user);
    
    void updateUserPassword(String id, String password);
    
    void disableUser(String id);
    
    void addRoleToUser(String userId, String roleId);
    
    void removeRoleFromUser(String userId, String roleId);
}