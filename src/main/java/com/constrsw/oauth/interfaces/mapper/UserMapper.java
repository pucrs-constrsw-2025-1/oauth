package com.constrsw.oauth.interfaces.mapper;

import com.constrsw.oauth.application.dto.user.UserRequest;
import com.constrsw.oauth.application.dto.user.UserResponse;
import com.constrsw.oauth.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toEntity(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPassword(userRequest.getPassword());
        user.setEnabled(true); // New users are enabled by default
        
        return user;
    }
    
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEnabled(user.isEnabled());
        
        return userResponse;
    }
}