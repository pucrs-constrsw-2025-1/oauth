package com.constrsw.oauth.application.service;

import com.constrsw.oauth.application.dto.user.UserRequest;
import com.constrsw.oauth.application.dto.user.UserResponse;
import com.constrsw.oauth.domain.entity.User;
import com.constrsw.oauth.domain.exception.DomainException;
import com.constrsw.oauth.domain.service.UserManagementService;
import com.constrsw.oauth.interfaces.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    private final UserManagementService userManagementService;
    private final UserMapper userMapper;
    
    @Autowired
    public UserService(UserManagementService userManagementService, UserMapper userMapper) {
        this.userManagementService = userManagementService;
        this.userMapper = userMapper;
    }
    
    public UserResponse createUser(UserRequest userRequest) {
        try {
            User user = userMapper.toEntity(userRequest);
            String userId = userManagementService.createUser(user);
            User createdUser = userManagementService.getUserById(userId);
            return userMapper.toResponse(createdUser);
        } catch (Exception e) {
            throw new DomainException("OA-101", "User creation failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public List<UserResponse> getAllUsers() {
        try {
            List<User> users = userManagementService.getAllUsers();
            return users.stream()
                    .map(userMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DomainException("OA-102", "Retrieving all users failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public List<UserResponse> getUsersByEnabled(boolean enabled) {
        try {
            List<User> users = userManagementService.getUsersByEnabled(enabled);
            return users.stream()
                    .map(userMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DomainException("OA-103", "Retrieving users by enabled status failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public UserResponse getUserById(String id) {
        try {
            User user = userManagementService.getUserById(id);
            return userMapper.toResponse(user);
        } catch (Exception e) {
            throw new DomainException("OA-104", "Retrieving user by id failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void updateUser(String id, UserRequest userRequest) {
        try {
            User user = userMapper.toEntity(userRequest);
            userManagementService.updateUser(id, user);
        } catch (Exception e) {
            throw new DomainException("OA-105", "User update failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void updateUserPassword(String id, String password) {
        try {
            userManagementService.updateUserPassword(id, password);
        } catch (Exception e) {
            throw new DomainException("OA-106", "User password update failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void disableUser(String id) {
        try {
            userManagementService.disableUser(id);
        } catch (Exception e) {
            throw new DomainException("OA-107", "User disabling failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void addRoleToUser(String userId, String roleId) {
        try {
            userManagementService.addRoleToUser(userId, roleId);
        } catch (Exception e) {
            throw new DomainException("OA-108", "Adding role to user failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
    
    public void removeRoleFromUser(String userId, String roleId) {
        try {
            userManagementService.removeRoleFromUser(userId, roleId);
        } catch (Exception e) {
            throw new DomainException("OA-109", "Removing role from user failed: " + e.getMessage(), "OAuthAPI", e);
        }
    }
}