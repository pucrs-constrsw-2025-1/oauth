package com.constrsw.oauth.service;

import com.constrsw.oauth.dto.UserDTO;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @SuppressWarnings("unchecked")
    public UserDTO extractUserDetails(Jwt jwt) {
        String userId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        
        List<String> roles = Collections.emptyList();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            roles = ((List<String>) realmAccess.get("roles"));
        }
        
        return UserDTO.builder()
                .id(userId)
                .username(username)
                .email(email)
                .name(name)
                .roles(roles)
                .additionalInfo(jwt.getClaims())
                .build();
    }
}