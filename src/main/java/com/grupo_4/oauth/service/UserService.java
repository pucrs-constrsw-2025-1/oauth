package com.grupo_4.oauth.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.grupo_4.oauth.config.KeycloakConfig;
import com.grupo_4.oauth.exception.UserCreationException;
import com.grupo_4.oauth.exception.UserFetchException;
import com.grupo_4.oauth.exception.UserNotFoundException;
import com.grupo_4.oauth.model.UserRequest;
import com.grupo_4.oauth.model.UserResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RestTemplate restTemplate;
    private final KeycloakConfig keycloakConfig;

    public UserResponse createUser(UserRequest userRequest, String accessToken) {
        if (userRequest == null) {
            throw new UserCreationException("User data cannot be null");
        }
        
        validateUserRequest(userRequest);
        
        log.info("Creating new user with username: {}", userRequest.getUsername());
        
        String adminUrl = keycloakConfig.getUsersAdminUrl();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        
        Map<String, Object> userRequestMap = createUserRequestMap(userRequest);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(userRequestMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    adminUrl,
                    requestEntity,
                    String.class);
            
            // Keycloak returns 201 Created with location header containing the user ID
            if (response.getStatusCode().is2xxSuccessful()) {
                String location = response.getHeaders().getLocation().toString();
                String userId = location.substring(location.lastIndexOf("/") + 1);
                
                UserResponse userResponse = new UserResponse();
                userResponse.setId(userId);
                userResponse.setUsername(userRequest.getUsername());
                userResponse.setEmail(userRequest.getEmail());
                userResponse.setFirstName(userRequest.getFirstName());
                userResponse.setLastName(userRequest.getLastName());
                userResponse.setEnabled(userRequest.isEnabled());
                userResponse.setEmailVerified(userRequest.isEmailVerified());
                
                log.info("Successfully created user with ID: {}", userId);
                return userResponse;
            } else {
                log.error("Failed to create user. Status code: {}", response.getStatusCode());
                throw new UserCreationException("Failed to create user: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException ex) {
            log.error("HTTP client error during user creation: {}", ex.getMessage(), ex);
            throw new UserCreationException("User creation failed: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            log.error("Error connecting to Keycloak: {}", ex.getMessage(), ex);
            throw new UserCreationException("Unable to connect to authentication service", ex);
        }
    }
    
    public List<UserResponse> getAllUsers(String accessToken) {
        log.info("Fetching all users");
        
        String adminUrl = keycloakConfig.getUsersAdminUrl();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        try {
            // Add query parameter to only return enabled users
            String urlWithParams = adminUrl + "?enabled=true";
            
            ResponseEntity<UserResponse[]> response = restTemplate.exchange(
                    urlWithParams,
                    HttpMethod.GET,
                    requestEntity,
                    UserResponse[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully fetched {} users", response.getBody().length);
                return Arrays.asList(response.getBody());
            } else {
                log.error("Failed to fetch users. Status code: {}", response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException ex) {
            log.error("HTTP client error while fetching users: {}", ex.getMessage(), ex);
            throw new UserFetchException("Failed to fetch users: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            log.error("Error connecting to Keycloak: {}", ex.getMessage(), ex);
            throw new UserFetchException("Unable to connect to authentication service", ex);
        }
    }
    
    public UserResponse getUserById(String userId, String accessToken) {
        if (userId == null || userId.isEmpty()) {
            throw new UserFetchException("User ID cannot be null or empty");
        }
        
        log.info("Fetching user with ID: {}", userId);
        
        String adminUrl = keycloakConfig.getUsersAdminUrl() + "/" + userId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    adminUrl,
                    HttpMethod.GET,
                    requestEntity,
                    UserResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully fetched user with ID: {}", userId);
                return response.getBody();
            } else {
                log.error("Failed to fetch user. Status code: {}", response.getStatusCode());
                throw new UserNotFoundException("User not found with ID: " + userId);
            }
        } catch (HttpClientErrorException.NotFound ex) {
            log.error("User not found with ID: {}", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        } catch (HttpClientErrorException ex) {
            log.error("HTTP client error while fetching user: {}", ex.getMessage(), ex);
            throw new UserFetchException("Failed to fetch user: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            log.error("Error connecting to Keycloak: {}", ex.getMessage(), ex);
            throw new UserFetchException("Unable to connect to authentication service", ex);
        }
    }
    
    private void validateUserRequest(UserRequest userRequest) {
        if (userRequest.getUsername() == null || userRequest.getUsername().isEmpty()) {
            throw new UserCreationException("Username is required");
        }
        
        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            throw new UserCreationException("Email is required");
        }
        
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            throw new UserCreationException("Password is required");
        }
    }
    
    private Map<String, Object> createUserRequestMap(UserRequest userRequest) {
        Map<String, Object> userRequestMap = new HashMap<>();
        userRequestMap.put("username", userRequest.getUsername());
        userRequestMap.put("email", userRequest.getEmail());
        userRequestMap.put("firstName", userRequest.getFirstName());
        userRequestMap.put("lastName", userRequest.getLastName());
        userRequestMap.put("enabled", userRequest.isEnabled());
        userRequestMap.put("emailVerified", userRequest.isEmailVerified());
        
        // Set credentials
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", userRequest.getPassword());
        credential.put("temporary", false);
        
        userRequestMap.put("credentials", new Object[]{credential});
        
        return userRequestMap;
    }
} 