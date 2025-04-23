package com.constrsw.oauth.controller;

import com.constrsw.oauth.dto.UserDTO;
import com.constrsw.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResourceController {

    private final UserService userService;

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Endpoint público, não precisa de autenticação");
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserDTO> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        UserDTO userDTO = userService.extractUserDetails(jwt);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Endpoint administrativo, necessário ter role ADMIN");
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok("Endpoint para usuários, necessário ter role USER");
    }
}