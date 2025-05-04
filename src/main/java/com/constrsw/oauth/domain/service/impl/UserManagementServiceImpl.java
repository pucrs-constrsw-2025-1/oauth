package com.constrsw.oauth.domain.service.impl;

import com.constrsw.oauth.domain.entity.User;
import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.repository.UserRepository;
import com.constrsw.oauth.domain.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserManagementServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public String createUser(User user) {
        validateUserForCreation(user);
        return userRepository.createUser(user);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }
    
    @Override
    public List<User> getUsersByEnabled(boolean enabled) {
        return userRepository.findUsersByEnabled(enabled);
    }
    
    @Override
    public User getUserById(String id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new DomainException("OA-404", "User not found with id: " + id, "OAuthAPI"));
    }
    
    @Override
    public void updateUser(String id, User user) {
        validateUserForUpdate(user);
        // Verify if the user exists
        userRepository.findUserById(id)
                .orElseThrow(() -> new DomainException("OA-404", "User not found with id: " + id, "OAuthAPI"));
        userRepository.updateUser(id, user);
    }
    
    @Override
    public void updateUserPassword(String id, String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new DomainException("OA-400", "Password cannot be empty", "OAuthAPI");
        }
        
        // Verify if the user exists
        userRepository.findUserById(id)
                .orElseThrow(() -> new DomainException("OA-404", "User not found with id: " + id, "OAuthAPI"));
        
        userRepository.updateUserPassword(id, password);
    }
    
    @Override
    public void disableUser(String id) {
        // Verify if the user exists
        userRepository.findUserById(id)
                .orElseThrow(() -> new DomainException("OA-404", "User not found with id: " + id, "OAuthAPI"));
        
        userRepository.disableUser(id);
    }
    
    @Override
    public void addRoleToUser(String userId, String roleId) {
        // Verify if the user exists
        userRepository.findUserById(userId)
                .orElseThrow(() -> new DomainException("OA-404", "User not found with id: " + userId, "OAuthAPI"));
        
        userRepository.addRoleToUser(userId, roleId);
    }
    
    @Override
    public void removeRoleFromUser(String userId, String roleId) {
        // Verify if the user exists
        userRepository.findUserById(userId)
                .orElseThrow(() -> new DomainException("OA-404", "User not found with id: " + userId, "OAuthAPI"));
        
        userRepository.removeRoleFromUser(userId, roleId);
    }
    
    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new DomainException("OA-400", "User cannot be null", "OAuthAPI");
        }
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new DomainException("OA-400", "Username cannot be empty", "OAuthAPI");
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new DomainException("OA-400", "Password cannot be empty", "OAuthAPI");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new DomainException("OA-400", "First name cannot be empty", "OAuthAPI");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new DomainException("OA-400", "Last name cannot be empty", "OAuthAPI");
        }
    }
    
    private void validateUserForUpdate(User user) {
        if (user == null) {
            throw new DomainException("OA-400", "User cannot be null", "OAuthAPI");
        }
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new DomainException("OA-400", "Username cannot be empty", "OAuthAPI");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new DomainException("OA-400", "First name cannot be empty", "OAuthAPI");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new DomainException("OA-400", "Last name cannot be empty", "OAuthAPI");
        }
    }
}