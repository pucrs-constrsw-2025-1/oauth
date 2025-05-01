package com.grupo_4.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo_4.oauth.model.UserRequest;
import com.grupo_4.oauth.model.UserResponse;
import com.grupo_4.oauth.model.UpdateUserRequest;
import com.grupo_4.oauth.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody UserRequest userRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("Received request to create user: {}", userRequest.getUsername());
        
        // Extract token from Authorization header
        String accessToken = authorizationHeader.replace("Bearer ", "");
        
        UserResponse createdUser = userService.createUser(userRequest, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("Received request to get all users");
        
        // Extract token from Authorization header
        String accessToken = authorizationHeader.replace("Bearer ", "");
        
        List<UserResponse> users = userService.getAllUsers(accessToken);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("Received request to get user with ID: {}", userId);
        
        // Extract token from Authorization header
        String accessToken = authorizationHeader.replace("Bearer ", "");
        
        UserResponse user = userService.getUserById(userId, accessToken);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest updateRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("Received request to update user with ID: {}", userId);
        
        // Extract token from Authorization header
        String accessToken = authorizationHeader.replace("Bearer ", "");
        
        userService.updateUser(userId, updateRequest, accessToken);
        return ResponseEntity.ok().build();
    }

    
}